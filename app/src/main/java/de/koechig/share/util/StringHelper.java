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
            String prefix = mail.split("@")[0];
            prefix = prefix.replaceAll("\\.", "");
            prefix = prefix.replaceAll("#", "");
            prefix = prefix.replaceAll("$", "");
            prefix = prefix.replaceAll("\\[", "");
            prefix = prefix.replaceAll("]", "");
            return prefix;
        } else {
            return null;
        }
    }
}
