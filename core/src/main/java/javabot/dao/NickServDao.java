package javabot.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javabot.model.NickServInfo;
import static javabot.model.NickServInfo.NSERV_DATE_FORMAT;
import javabot.model.criteria.NickServInfoCriteria;
import javabot.model.criteria.NickServInfoCriteria.NickServInfoUpdater;

public class NickServDao extends BaseDao<NickServInfo> {
  protected NickServDao() {
    super(NickServInfo.class);
  }

  public void clear() {
    ds.delete(getQuery());
  }

  public void process(final List<String> list) {
    NickServInfo info = new NickServInfo();
    String[] split = list.get(0).split(" ");
    info.setNick(split[2]);
    info.setAccount(split[4].substring(0, split[4].indexOf(')')));
    list.subList(1, list.size()).stream().filter(line -> line.contains(":")).forEach(line -> {
      int i = line.indexOf(':');
      String key = line.substring(0, i).trim();
      String value = line.substring(i + 1).trim();
      if (key.equalsIgnoreCase("Registered")) {
        info.setRegistered(extractDate(value));
      } else if (key.equalsIgnoreCase("User Reg.")) {
        info.setUserRegistered(extractDate(value));
      } else if (key.equalsIgnoreCase("Last seen")) {
        info.setLastSeen(extractDate(value));
      } else if (key.equalsIgnoreCase("Last addr")) {
        info.setLastAddress(value);
      } else {
        info.extra(key.replace(".", ""), value);
      }
    });
    NickServInfo nickServInfo = findByAccount(info.getAccount());
    if (nickServInfo != null) {
      nickServInfo.setNick(info.getNick());
      nickServInfo.setLastSeen(info.getLastSeen());
      nickServInfo.setLastAddress(info.getLastAddress());
      save(nickServInfo);
    } else {
      save(info);
    }
  }

  private LocalDateTime extractDate(final String line) {
    if (line.endsWith("now")) {
      return LocalDateTime.now();
    } else {
      String dateString = line.contains("(") ? line.substring(0, line.indexOf(" (")) : line;
      return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(NSERV_DATE_FORMAT));
    }
  }

  public NickServInfo findByAccount(final String account) {
    NickServInfoCriteria criteria = new NickServInfoCriteria(ds);
    criteria.account(account);
    return criteria.query().get();
  }

  public NickServInfo findByNick(final String nick) {
    NickServInfoCriteria criteria = new NickServInfoCriteria(ds);
    criteria.nick(nick);
    return criteria.query().get();
  }

  public NickServInfo updateNick(final String oldNick, final String newNick) {
    NickServInfoCriteria criteria = new NickServInfoCriteria(ds);
    criteria.nick(oldNick);
    NickServInfoUpdater updater = criteria.getUpdater();
    updater.nick(newNick);
    updater.update();
    criteria = new NickServInfoCriteria(ds);
    criteria.nick(newNick);
    return criteria.query().get();
  }

  public void unregister(final String nick) {
    NickServInfoCriteria criteria = new NickServInfoCriteria(ds);
    criteria.nick(nick);
    criteria.delete();
  }
}
