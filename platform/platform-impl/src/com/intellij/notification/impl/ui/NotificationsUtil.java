/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.notification.impl.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

/**
 * @author spleaner
 */
public class NotificationsUtil {
  private static final Logger LOG = Logger.getInstance("#com.intellij.notification.impl.ui.NotificationsUtil");
  private static final int TITLE_LIMIT = 1000;
  private static final int CONTENT_LIMIT = 10000;

  @NotNull
  public static String buildHtml(@NotNull final Notification notification, @Nullable String style) {
    String title = notification.getTitle();
    String content = notification.getContent();
    if (title.length() > TITLE_LIMIT || content.length() > CONTENT_LIMIT) {
      LOG.info("Too large notification " + notification + " of " + notification.getClass() +
               "\nListener=" + notification.getListener() +
               "\nTitle=" + title +
               "\nContent=" + content);
      title = StringUtil.trimLog(title, TITLE_LIMIT);
      content = StringUtil.trimLog(content, CONTENT_LIMIT);
    }
    return buildHtml(title, null, content, style, "#" + ColorUtil.toHex(getMessageType(notification).getTitleForeground()), null, null);
  }

  @NotNull
  public static String buildHtml(@NotNull final Notification notification,
                                 @Nullable String style,
                                 boolean isContent,
                                 @Nullable Color color,
                                 @Nullable String contentStyle) {
    String title = !isContent ? notification.getTitle() : "";
    String subtitle = !isContent ? notification.getSubtitle() : null;
    String content = isContent ? notification.getContent() : "";
    if (title.length() > TITLE_LIMIT || StringUtil.length(subtitle) > TITLE_LIMIT || content.length() > CONTENT_LIMIT) {
      LOG.info("Too large notification " + notification + " of " + notification.getClass() +
               "\nListener=" + notification.getListener() +
               "\nTitle=" + title +
               "\nSubtitle=" + subtitle +
               "\nContent=" + content);
      title = StringUtil.trimLog(title, TITLE_LIMIT);
      subtitle = StringUtil.trimLog(StringUtil.notNullize(subtitle), TITLE_LIMIT);
      content = StringUtil.trimLog(content, CONTENT_LIMIT);
    }
    String colorText = color == null ? null : "#" + ColorUtil.toHex(color);
    return buildHtml(title, subtitle, content, style, isContent ? null : colorText, isContent ? colorText : null, contentStyle);
  }

  public static String buildHtml(@Nullable String title, @Nullable String subtitle, @Nullable String content, @Nullable String style) {
    return buildHtml(title, subtitle, content, style, null, null, null);
  }

  @NotNull
  public static String buildHtml(@Nullable String title,
                                  @Nullable String subtitle,
                                  @Nullable String content,
                                  @Nullable String style,
                                  @Nullable String titleColor,
                                  @Nullable String contentColor,
                                  @Nullable String contentStyle) {
    if (StringUtil.isEmpty(title) && !StringUtil.isEmpty(subtitle)) {
      title = subtitle;
      subtitle = null;
    }
    else if (!StringUtil.isEmpty(title) && !StringUtil.isEmpty(subtitle)) {
      title += ":";
    }

    StringBuilder result = new StringBuilder();
    if (style != null) {
      result.append("<div style=\"").append(style).append("\">");
    }
    if (!StringUtil.isEmpty(title)) {
      result.append("<b").append(titleColor == null ? ">" : " color=\"" + titleColor + "\">").append(title).append("</b>");
    }
    if (!StringUtil.isEmpty(subtitle)) {
      result.append("&nbsp;").append(subtitle);
    }
    if (!StringUtil.isEmpty(content)) {
      result.append("<p").append(contentStyle == null ? "" : " style=\"" + contentStyle + "\"")
        .append(contentColor == null ? ">" : " color=\"" + contentColor + "\">").append(content).append("</p>");
    }
    if (style != null) {
      result.append("</div>");
    }
    return XmlStringUtil.wrapInHtml(result.toString());
  }

  @Nullable
  public static String getFontStyle() {
    Pair<String, Integer> systemFontData = UIUtil.getSystemFontData();
    return systemFontData == null ? null : "font-family:" + systemFontData.first + ";";
  }

  @Nullable
  public static HyperlinkListener wrapListener(@NotNull final Notification notification) {
    final NotificationListener listener = notification.getListener();
    if (listener == null) return null;

    return new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          final NotificationListener listener1 = notification.getListener();
          if (listener1 != null) {
            listener1.hyperlinkUpdate(notification, e);
          }
        }
      }
    };
  }

  @NotNull
  public static Icon getIcon(@NotNull final Notification notification) {
    Icon icon = notification.getIcon();
    return icon != null ? icon : getMessageType(notification).getDefaultIcon();
  }

  @NotNull
  public static MessageType getMessageType(@NotNull Notification notification) {
    switch (notification.getType()) {
      case WARNING: return MessageType.WARNING;
      case ERROR: return MessageType.ERROR;
      case INFORMATION:
      default: return MessageType.INFO;
    }
  }

  @NotNull
  public static Color getBackground(@NotNull final Notification notification) {
    return getMessageType(notification).getPopupBackground();
  }

  @NotNull
  public static Color getBorderColor(@NotNull Notification notification) {
    switch (notification.getType()) {
      case ERROR:
        return new JBColor(Color.gray, new Color(0xc8c8c8));
      case WARNING:
        return new JBColor(Color.gray, new Color(0x615f51));
      case INFORMATION:
      default:
        return new JBColor(Color.gray, new Color(0x205c00));
    }
  }
}
