/*
 * Created on 14 juil. 2003
 * (c)2003 Jérôme Nègre - http://www.jnegre.org/
 *
 */
package org.jnegre.allthenews.pref;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.jnegre.allthenews.Plugin;

/**
 * @author jerome
 *
 */
public class ListEncoder {

    public static String[] decode(String stringList) {
        StringTokenizer tokenizer = new StringTokenizer(stringList, " ");
        int countTokens = tokenizer.countTokens();
        String[] result = new String[countTokens];
        try {
            for (int i = 0; i < countTokens; i++) {
                result[i] = URLDecoder.decode(tokenizer.nextToken(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            Plugin.logError("Internal Error", e);
        }
        return result;
    }

    public static String encode(String[] items) {
        StringBuffer result = new StringBuffer();
        try {
            for (int i = 0; i < items.length; i++) {
                result.append(URLEncoder.encode(items[i], "UTF-8")).append(' ');
            }
        } catch (UnsupportedEncodingException e) {
            Plugin.logError("Internal Error", e);
        }
        return result.toString();
    }

}
