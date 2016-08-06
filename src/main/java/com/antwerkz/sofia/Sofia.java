package com.antwerkz.sofia;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.security.*;
import java.util.ResourceBundle.Control;

import org.slf4j.*;

public class Sofia {
    private static final Map<Locale, ResourceBundle> messages = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Sofia.class);

    private static ResourceBundle getBundle(Locale... localeList) {
        Locale locale = localeList.length == 0 ? Locale.getDefault() : localeList[0];
        ResourceBundle labels = Sofia.loadBundle(locale);
        if(labels == null) {
            labels = Sofia.loadBundle(Locale.ROOT);
        }
        return labels;
    }

    private static ResourceBundle loadBundle(Locale locale) {
        ResourceBundle bundle = Sofia.messages.get(locale);
        if(bundle == null) {
            bundle = ResourceBundle.getBundle("sofia", locale );
            Sofia.messages.put(locale, bundle);
        }
        return bundle;
    }

    private static String getMessageValue(String key, Locale... locale) {
        return (String) Sofia.getBundle(locale).getObject(key);
    }

    public static String loggingInUser(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("@debug.logging.in.user", locale), arg0);
    }

    public static void logLoggingInUser(Object arg0, Locale... locale) {
        if(Sofia.logger.isDebugEnabled()) {
            Sofia.logger.debug(Sofia.loggingInUser(arg0));
        }
    }
    public static String factoidInvalidSearchValue(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("@info.factoid.invalid.search.value", locale), arg0);
    }

    public static void logFactoidInvalidSearchValue(Object arg0, Locale... locale) {
        if(Sofia.logger.isInfoEnabled()) {
            Sofia.logger.info(Sofia.factoidInvalidSearchValue(arg0));
        }
    }
    public static String noNickservEntry(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("@info.no.nickserv.entry", locale), arg0);
    }

    public static void logNoNickservEntry(Object arg0, Locale... locale) {
        if(Sofia.logger.isInfoEnabled()) {
            Sofia.logger.info(Sofia.noNickservEntry(arg0));
        }
    }
    public static String waitingForNickserv(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("@info.waiting.for.nickserv", locale), arg0);
    }

    public static void logWaitingForNickserv(Object arg0, Locale... locale) {
        if(Sofia.logger.isInfoEnabled()) {
            Sofia.logger.info(Sofia.waitingForNickserv(arg0));
        }
    }
    public static String webappNotStarting(Locale... locale) {
        return Sofia.getMessageValue("@info.webapp.not.starting", locale);
    }

    public static void logWebappNotStarting(Locale... locale) {
        if(Sofia.logger.isInfoEnabled()) {
            Sofia.logger.info(Sofia.webappNotStarting());
        }
    }
    public static String webappStarting(Locale... locale) {
        return Sofia.getMessageValue("@info.webapp.starting", locale);
    }

    public static void logWebappStarting(Locale... locale) {
        if(Sofia.logger.isInfoEnabled()) {
            Sofia.logger.info(Sofia.webappStarting());
        }
    }
    public static String accountTooNew(Locale... locale) {
        return Sofia.getMessageValue("account.too.new", locale);
    }

    public static String action(Locale... locale) {
        return Sofia.getMessageValue("action", locale);
    }

    public static String addedBy(Locale... locale) {
        return Sofia.getMessageValue("addedBy", locale);
    }

    public static String addedOn(Locale... locale) {
        return Sofia.getMessageValue("addedOn", locale);
    }

    public static String adminAdded(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.added", locale), arg0);
    }

    public static String adminAlready(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.already", locale), arg0);
    }

    public static String adminBadChannelName(Locale... locale) {
        return Sofia.getMessageValue("admin.badChannelName", locale);
    }

    public static String adminDoneRemovingOldJavadoc(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.done.removing.old.javadoc", locale), arg0);
    }

    public static String adminJoinedChannel(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.joinedChannel", locale), arg0);
    }

    public static String adminJoiningChannel(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.joiningChannel", locale), arg0);
    }

    public static String adminJoiningLoggedChannel(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.joiningLoggedChannel", locale), arg0);
    }

    public static String adminKnownCommands(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.known.commands", locale), arg0, arg1);
    }

    public static String adminKnownOperations(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.known.operations", locale), arg0, arg1);
    }

    public static String adminListChannelsPreamble(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.list.channels.preamble", locale), arg0);
    }

    public static String adminOperationDisabled(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.operation.disabled", locale), arg0);
    }

    public static String adminOperationEnabled(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.operation.enabled", locale), arg0);
    }

    public static String adminOperationInstructions(Locale... locale) {
        return Sofia.getMessageValue("admin.operation.instructions", locale);
    }

    public static String adminOperationNotDisabled(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.operation.not.disabled", locale), arg0);
    }

    public static String adminOperationNotEnabled(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.operation.not.enabled", locale), arg0);
    }

    public static String adminParseFailure(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.parseFailure", locale), arg0);
    }

    public static String adminRemovingOldJavadoc(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.removing.old.javadoc", locale), arg0);
    }

    public static String adminRunningOperations(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("admin.running.operations", locale), arg0);
    }

    public static String alreadyShunned(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("alreadyShunned", locale), arg0);
    }

    public static String apiLocation(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("api.location", locale), arg0, arg1);
    }

    public static String botAolbonics(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("bot.aolbonics", locale), arg0);
    }

    public static String botIgnoring(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("bot.ignoring", locale), arg0);
    }

    public static String botSelfTalk(Locale... locale) {
        return Sofia.getMessageValue("bot.selfTalk", locale);
    }

    public static String botStats(Number arg0, Number arg1, Number arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("bot.stats", locale), arg0, arg1, arg2);
    }

    public static String botUnixCommand(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("bot.unixCommand", locale), arg0, arg1);
    }

    public static String botVersion(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("bot.version", locale), arg0);
    }

    public static String channelDeleted(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("channel.deleted", locale), arg0);
    }

    public static String channelUnknown(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("channel.unknown", locale), arg0, arg1);
    }

    public static String channelUpdated(Locale... locale) {
        return Sofia.getMessageValue("channel.updated", locale);
    }

    public static String configIrcHistory(Locale... locale) {
        return Sofia.getMessageValue("config.irc.history", locale);
    }

    public static String configIrcNick(Locale... locale) {
        return Sofia.getMessageValue("config.irc.nick", locale);
    }

    public static String configIrcPassword(Locale... locale) {
        return Sofia.getMessageValue("config.irc.password", locale);
    }

    public static String configIrcPort(Locale... locale) {
        return Sofia.getMessageValue("config.irc.port", locale);
    }

    public static String configIrcServer(Locale... locale) {
        return Sofia.getMessageValue("config.irc.server", locale);
    }

    public static String configIrcTrigger(Locale... locale) {
        return Sofia.getMessageValue("config.irc.trigger", locale);
    }

    public static String configMinimumNickservAge(Locale... locale) {
        return Sofia.getMessageValue("config.minimum.nickserv.age", locale);
    }

    public static String configThrottleThreshold(Locale... locale) {
        return Sofia.getMessageValue("config.throttle.threshold", locale);
    }

    public static String configWebUrl(Locale... locale) {
        return Sofia.getMessageValue("config.web.url", locale);
    }

    public static String configurationMissingProperties(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("configuration.missing.properties", locale), arg0);
    }

    public static String configurationSetProperty(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("configuration.set.property", locale), arg0, arg1);
    }

    public static String configurationUnknownProperty(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("configuration.unknown.property", locale), arg0);
    }

    public static String configurationWebMissingFile(Locale... locale) {
        return Sofia.getMessageValue("configuration.web.missing.file", locale);
    }

    public static String daysUntil(Object arg0, Number arg1, Date arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("days.until", locale), arg0, arg1, arg2);
    }

    public static String email(Locale... locale) {
        return Sofia.getMessageValue("email", locale);
    }

    public static String factoidAdded(Object arg0, Object arg1, Object arg2, Object arg3, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.added", locale), arg0, arg1, arg2, arg3);
    }

    public static String factoidCantBeBlank(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.cantBeBlank", locale), arg0);
    }

    public static String factoidChanged(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.changed", locale), arg0, arg1, arg2, arg3, arg4);
    }

    public static String factoidChangingLocked(Object arg0, Object arg1, Object arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.changing.locked", locale), arg0, arg1, arg2);
    }

    public static String factoidDeleteLocked(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.delete.locked", locale), arg0);
    }

    public static String factoidDeleteUnknown(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.delete.unknown", locale), arg0, arg1);
    }

    public static String factoidExists(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.exists", locale), arg0, arg1);
    }

    public static String factoidForgotten(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.forgotten", locale), arg0, arg1);
    }

    public static String factoidInfo(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.info", locale), arg0, arg1, arg2, arg3, arg4);
    }

    public static String factoidInvalidName(Locale... locale) {
        return Sofia.getMessageValue("factoid.invalid.name", locale);
    }

    public static String factoidInvalidValue(Locale... locale) {
        return Sofia.getMessageValue("factoid.invalid.value", locale);
    }

    public static String factoidLocked(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.locked", locale), arg0);
    }

    public static String factoidLoop(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.loop", locale), arg0);
    }

    public static String factoidRemoved(Object arg0, Object arg1, Object arg2, Object arg3, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.removed", locale), arg0, arg1, arg2, arg3);
    }

    public static String factoidTellSyntax(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.tell.syntax", locale), arg0);
    }

    public static String factoidUnknown(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("factoid.unknown", locale), arg0);
    }

    public static String historyLength(Locale... locale) {
        return Sofia.getMessageValue("history.length", locale);
    }

    public static String hostName(Locale... locale) {
        return Sofia.getMessageValue("hostName", locale);
    }

    public static String invalidDateFormat(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("invalid.date.format", locale), arg0);
    }

    public static String ircName(Locale... locale) {
        return Sofia.getMessageValue("ircName", locale);
    }

    public static String javadocApiList(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("javadoc.api.list", locale), arg0, arg1);
    }

    public static String javadocApiName(Locale... locale) {
        return Sofia.getMessageValue("javadoc.api.name", locale);
    }

    public static String javadocDownloadUrl(Locale... locale) {
        return Sofia.getMessageValue("javadoc.download.url", locale);
    }

    public static String javadocUrl(Locale... locale) {
        return Sofia.getMessageValue("javadoc.url", locale);
    }

    public static String jsrInvalid(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("jsr.invalid", locale), arg0);
    }

    public static String jsrMissing(Locale... locale) {
        return Sofia.getMessageValue("jsr.missing", locale);
    }

    public static String jsrUnknown(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("jsr.unknown", locale), arg0);
    }

    public static String karmaChanged(Object arg0, Object arg1, Number arg2, Object arg3, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("karma.changed", locale), arg0, arg1, arg2, arg3);
    }

    public static String karmaOthersNone(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("karma.others.none", locale), arg0, arg1);
    }

    public static String karmaOthersValue(Object arg0, Number arg1, Object arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("karma.others.value", locale), arg0, arg1, arg2);
    }

    public static String karmaOwnIncrement(Locale... locale) {
        return Sofia.getMessageValue("karma.own.increment", locale);
    }

    public static String karmaOwnNone(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("karma.own.none", locale), arg0);
    }

    public static String karmaOwnValue(Object arg0, Number arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("karma.own.value", locale), arg0, arg1);
    }

    public static String leaveChannel(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("leave.channel", locale), arg0);
    }

    public static String leavePrivmsg(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("leave.privmsg", locale), arg0);
    }

    public static String logsAnchorFormat(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("logs.anchorFormat", locale), arg0, arg1);
    }

    public static String logsEntry(Object arg0, Object arg1, Object arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("logs.entry", locale), arg0, arg1, arg2);
    }

    public static String logsNone(Locale... locale) {
        return Sofia.getMessageValue("logs.none", locale);
    }

    public static String logsNoneForNick(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("logs.noneForNick", locale), arg0);
    }

    public static String mavenArtifact(Locale... locale) {
        return Sofia.getMessageValue("maven.artifact", locale);
    }

    public static String mavenDependency(Locale... locale) {
        return Sofia.getMessageValue("maven.dependency", locale);
    }

    public static String mavenGroup(Locale... locale) {
        return Sofia.getMessageValue("maven.group", locale);
    }

    public static String mavenVersion(Locale... locale) {
        return Sofia.getMessageValue("maven.version", locale);
    }

    public static String nickservNotResponding(Locale... locale) {
        return Sofia.getMessageValue("nickserv.not.responding", locale);
    }

    public static String noDocumentation(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("no.documentation", locale), arg0);
    }

    public static String notAdmin(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("not.admin", locale), arg0);
    }

    public static String notAllowed(Locale... locale) {
        return Sofia.getMessageValue("not.allowed", locale);
    }

    public static String ok(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("ok", locale), arg0);
    }

    public static String privmsgChange(Locale... locale) {
        return Sofia.getMessageValue("privmsg.change", locale);
    }

    public static String registerNick(Object arg0, Object arg1, Object arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("register.nick", locale), arg0, arg1, arg2);
    }

    public static String rfcFail(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("rfc.fail", locale), arg0);
    }

    public static String rfcInvalid(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("rfc.invalid", locale), arg0);
    }

    public static String rfcSucceed(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("rfc.succeed", locale), arg0, arg1);
    }

    public static String seenLast(Object arg0, Object arg1, Object arg2, Object arg3, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("seen.last", locale), arg0, arg1, arg2, arg3);
    }

    public static String seenUnknown(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("seen.unknown", locale), arg0, arg1);
    }

    public static String shunUsage(Locale... locale) {
        return Sofia.getMessageValue("shun.usage", locale);
    }

    public static String shunned(Object arg0, Date arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("shunned", locale), arg0, arg1);
    }

    public static String submit(Locale... locale) {
        return Sofia.getMessageValue("submit", locale);
    }

    public static String throttledUser(Locale... locale) {
        return Sofia.getMessageValue("throttled.user", locale);
    }

    public static String tooManyResults(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("too.many.results", locale), arg0);
    }

    public static String unhandledMessage(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("unhandled.message", locale), arg0);
    }

    public static String unknownApi(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("unknown.api", locale), arg0, arg1);
    }

    public static String unknownUser(Locale... locale) {
        return Sofia.getMessageValue("unknown.user", locale);
    }

    public static String userJoined(Object arg0, Object arg1, Object arg2, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.joined", locale), arg0, arg1, arg2);
    }

    public static String userNickChanged(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.nickChanged", locale), arg0, arg1);
    }

    public static String userNoSharedChannels(Locale... locale) {
        return Sofia.getMessageValue("user.no.shared.channels", locale);
    }

    public static String userNotInChannel(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.not.in.channel", locale), arg0, arg1);
    }

    public static String userNotFound(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.notFound", locale), arg0);
    }

    public static String userParted(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.parted", locale), arg0, arg1);
    }

    public static String userQuit(Object arg0, Object arg1, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("user.quit", locale), arg0, arg1);
    }

    public static String weatherUnknown(Object arg0, Locale... locale) {
        return MessageFormat.format(Sofia.getMessageValue("weather.unknown", locale), arg0);
    }


}
