package de.koechig.share.util;

import android.text.TextUtils;

/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class StringHelper {
    public boolean isMail(String mail) {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    public boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password);
    }

    public String getIdFromMail(String mail) {
        if (isMail(mail)) {
            String prefix = getMailPrefix(mail);
            prefix = removeUnallowedFirebaseKeyChars(prefix);
            return prefix;
        } else {
            return null;
        }
    }

    public String convertToId(String candidate) {
        String id = null;
        if (candidate != null && !candidate.isEmpty()) {
            id = removeUnallowedFirebaseKeyChars(candidate);
            if (!id.isEmpty()) {
                id = id.toLowerCase();
            } else {
                id = null;
            }
        }
        return id;
    }

    private String getMailPrefix(String mail) {
        return isMail(mail) ? mail.split("@")[0] : null;
    }

    private String removeUnallowedFirebaseKeyChars(String prefix) {
        String value = prefix.replaceAll("\\.", "");
        value = value.replaceAll("#", "");
        value = value.replaceAll("$", "");
        value = value.replaceAll("\\[", "");
        value = value.replaceAll("]", "");
        return value;
    }

    public String getFirstNameFromMail(String mail) {
        if (isMail(mail)) {
            String prefix = getMailPrefix(mail);
            if (prefix != null) {
                String[] arr;
                if (prefix.contains(".")) {
                    arr = prefix.split("\\.");
                } else if (prefix.contains("-")) {
                    arr = prefix.split("-");
                } else if (prefix.contains("_")) {
                    arr = prefix.split("_");
                } else {
                    arr = null;
                }
                if (arr != null && arr.length >= 1) {
                    String name = arr[0];
                    return capitalizeFirstLetter(name);
                }
            }
        }
        return null;
    }

    public String getLastNameFromMail(String mail) {
        if (isMail(mail)) {
            String prefix = getMailPrefix(mail);
            if (prefix != null) {
                String[] arr;
                if (prefix.contains(".")) {
                    arr = prefix.split("\\.");
                } else if (prefix.contains("-")) {
                    arr = prefix.split("-");
                } else if (prefix.contains("_")) {
                    arr = prefix.split("_");
                } else {
                    arr = null;
                }
                if (arr != null && arr.length >= 2) {
                    String name = arr[arr.length - 1];
                    return capitalizeFirstLetter(name);
                }
            }
        }
        return null;
    }

    public String capitalizeFirstLetter(String name) {
        if (name != null) {
            char[] arr = name.toCharArray();
            if (arr.length > 0) {
                String firstChar = String.valueOf(arr[0]);
                arr[0] = firstChar.toUpperCase().toCharArray()[0];
                name = new String(arr);
            }
        }
        return name;
    }
}
