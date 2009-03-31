package javabot.javadoc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import javabot.dao.ClazzDao;
import javabot.model.Persistent;
import org.cyberneko.html.parsers.DOMParser;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.html.HTMLElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Entity
@Table(name = "classes")
@NamedQueries({
    @NamedQuery(name = ClazzDao.DELETE_ALL, query = "delete from Clazz c where c.api=:api"),
    @NamedQuery(name = ClazzDao.DELETE_ALL_METHODS, query = "delete from Method m where m.clazz.api=:api"),
    @NamedQuery(name = ClazzDao.GET_BY_NAME, query = "select c from Clazz c where "
        + " upper(c.className)=:name"),
    @NamedQuery(name = ClazzDao.GET_BY_API_PACKAGE_AND_NAME, query = "select c from Clazz c where "
        + " c.packageName=:package and c.api=:api and c.className=:name"),
    @NamedQuery(name = ClazzDao.GET_BY_PACKAGE_AND_NAME, query = "select c from Clazz c where "
        + " upper(c.packageName)=upper(:package) and upper(c.className)=upper(:name)"),
    @NamedQuery(name = ClazzDao.GET_METHOD_NO_SIG, query = "select m from Method m where "
        + "m.clazz.id=:classId and upper(m.methodName)=:name order by m.shortSignatureStripped"),
    @NamedQuery(name = ClazzDao.GET_METHOD, query = "select m from Clazz c join c.methods m where "
        + "m.clazz.id=:classId and upper(m.methodName)=:name and (upper(m.shortSignatureTypes)=:params"
        + " or upper(m.shortSignatureStripped)=:params or upper(m.longSignatureTypes)=:params"
        + " or upper(m.longSignatureStripped)=:params) order by m.shortSignatureStripped")
})
public class Clazz extends JavadocElement implements Persistent {
    private static final Logger log = LoggerFactory.getLogger(Clazz.class);
    private Long id;
    private Api api;
    private String packageName;
    private String className;
    private Clazz superClass;
    private List<Method> methods = new ArrayList<Method>();

    public Clazz() {
    }

    public Clazz(final Api classApi, final HTMLElement element, final List<String> packages) {
        final String href = element.getAttribute("href").replace(".html", "").replace("../", "").replace("/", ".");
        String[] values = calculateNameAndPackage(href);
        packageName = values[0];
        className = values[1];
        boolean valid = packages.isEmpty();
        for (final String aPackage : packages) {
            valid |= packageName.startsWith(aPackage);
        }
        if (!valid) {
            throw new IrrelevantClassException(this + " is a class we don't care about");
        }
        setLongUrl(classApi.getBaseUrl() + sanitize(element));
        api = classApi;
//        classApi.getClasses().add(this);
    }

    public static String[] calculateNameAndPackage(final String href) {
        String clsName = href;
        while (clsName.contains(".") && Character.isLowerCase(clsName.charAt(0))) {
            clsName = clsName.substring(clsName.indexOf(".") + 1);
        }
        String pkgName = href.equals(clsName) ? null : href.substring(0, href.indexOf(clsName) - 1);
        return new String[]{pkgName, clsName};
    }

    private String sanitize(final HTMLElement element) {
        String url = element.getAttribute("href");
        while (url.startsWith("../")) {
            url = url.substring(3);
        }
        return url;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(final Long classId) {
        id = classId;
    }

    public Clazz(final Api apiName, final String pkg, final String name) {
        api = apiName;
        packageName = pkg;
        className = name;
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    public final List<Clazz> populate(final ClazzDao dao) {
        final List<Clazz> nested = new ArrayList<Clazz>();
        try {
//            log.debug("populating " + this);
            final Document document = getDocument(getLongUrl());
            final List<HTMLElement> dts = (List<HTMLElement>) new DOMXPath("//DT/following-sibling::node()"
                + "[text()='extends ']").evaluate(document);
            if (!dts.isEmpty()) {
                final HTMLElement element = dts.get(0);
                final HTMLElement aNode = (HTMLElement) element.getChildNodes().item(1);
                String pkg = aNode.getAttribute("href")
                    .replace(api.getBaseUrl(), "")
                    .replace(".html", "")
                    .replace("../", "")
                    .replace("/", ".");
                if (!pkg.startsWith("http")) {
                    while (!Character.isLetter(pkg.charAt(0))) {
                        pkg = pkg.substring(1);
                    }
                    final String[] values = calculateNameAndPackage(pkg);
                    final Clazz[] aClass = dao.getClass(values[0], values[1]);
                    if (aClass.length == 0) {
                        nested.add(this);
                    } else {
                        if (api.equals(aClass[0].getApi())) {
                            superClass = aClass[0];
                        }
                    }
                }
            }
            if (nested.isEmpty()) {
                final List<HTMLElement> result = (List<HTMLElement>) new DOMXPath("//HEAD/META[@name='keywords']")
                    .evaluate(document);
/*
                final List<HTMLElement> nestedElements = (List<HTMLElement>) new DOMXPath(
                    "//A[@name='nested_class_summary']")
                    .evaluate(document);
                if (!nestedElements.isEmpty()) {
                    final HTMLTableElement table = (HTMLTableElement) nestedElements.get(0).getNextSibling()
                        .getNextSibling();
                    final HTMLDocumentImpl doc = new HTMLDocumentImpl();
                    final HTMLElement element = (HTMLElement) table.cloneNode(true);
                    doc.adoptNode(element);
                    doc.setBody(element);
                    final List<HTMLElement> list = (List<HTMLElement>) new DOMXPath("//TD/CODE/B/A")
                        .evaluate(doc);
                    for (final HTMLElement htmlElement : list) {
                        if (!htmlElement.getAttribute("title").contains("type parameter")) {
                            final Clazz clazz = new Clazz(getApi(), htmlElement, Collections.<String>emptyList());
                            dao.save(clazz);
                            nested.add(clazz);
                        }
                    }
                }
*/
                for (final HTMLElement element : result) {
                    String content = element.getAttribute("content");
                    if (content.endsWith("()")) {
                        content = content.substring(0, content.length() - 2);
                        final List<HTMLElement> methodList = (List<HTMLElement>) new DOMXPath(
                            String.format("//A[starts-with(@name, '%s(')]", content)).evaluate(document);
                        final List<Method> overloads = new ArrayList<Method>();
                        for (final HTMLElement htmlElement : methodList) {
                            final NamedNodeMap attributes = htmlElement.getAttributes();
                            final Method method = new Method(attributes.getNamedItem("name").getNodeValue(), this);
                            dao.save(method);
                            overloads.add(method);
                        }
                        methods.addAll(overloads);
                    }
                }
                dao.save(this);
            }
        } catch (RuntimeException e) {
            log.debug(e.getMessage(), e);
        } catch (JaxenException e) {
            log.debug(e.getMessage(), e);
//            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            nested.add(this);
            log.error("Rescheduling " + className);
        } catch (SAXException e) {
            log.error(e.getMessage(), e);
//            throw new RuntimeException(e.getMessage());
        }
        return nested;
    }

    public static Document getDocument(final String specUrl) throws IOException, SAXException {
        final URL url = new URL(specUrl);
        final URLConnection connection = url.openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        final DOMParser parser = new DOMParser();
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            final InputSource inputSource = new InputSource(inputStream);
            parser.parse(inputSource);
        } catch (SAXException e) {
            log.debug(e.getMessage(), e);
            log.debug("specUrl = " + specUrl);
            throw e;
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            log.debug("specUrl = " + specUrl);
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return parser.getDocument();
    }

    @ManyToOne
    public Api getApi() {
        return api;
    }

    public void setApi(final Api api) {
        this.api = api;
    }

    @Override
    @Transient
    public String getApiName() {
        return getApi().getName();
    }

    @Column(nullable = false)
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String name) {
        packageName = name;
    }

    @Column(nullable = false)
    public String getClassName() {
        return className;
    }

    public void setClassName(final String name) {
        className = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Clazz getSuperClass() {
        return superClass;
    }

    public void setSuperClass(final Clazz aClass) {
        superClass = aClass;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "clazz", fetch = FetchType.EAGER)
    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(final List<Method> list) {
        methods = list;
    }

    @Override
    public String toString() {
        return packageName + "." + className;
    }
}