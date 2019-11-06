package fr.frezilla.watsonhit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
class WatsonHitParametersChecker {

    private static void addIfNotEmpty(@NonNull List<String> errorMessages, String s) {
        if (s != null) {
            errorMessages.add(s);
        }
    }

    static List<String> check(@NonNull WatsonHitParameters parameters) {
        List<String> errorMessages = new ArrayList<>();

        addIfNotEmpty(errorMessages, checkDescriptorFile(parameters.getCsvDescriptorFile()));
        addIfNotEmpty(errorMessages, checkCsvFile(parameters.getCsvFile()));
        addIfNotEmpty(errorMessages, checkResultFile(parameters.getResultFile()));

        return errorMessages;
    }

    private static String checkCsvFile(String fileName) {
        String msg = null;
        if (StringUtils.isEmpty(StringUtils.trim(fileName))) {
            msg = "le nom du fichier csv n'est pas correctement renseigné";
        } else {
            String m = checkFileToRead(new File(fileName));
            if (m != null) {
                msg = String.format(m, fileName);
            }
        }
        return msg;
    }

    private static String checkDescriptorFile(String fileName) {
        String msg = null;
        if (StringUtils.isEmpty(StringUtils.trim(fileName))) {
            msg = "le nom du fichier de description n'est pas correctement renseigné";
        } else {
            String m = checkFileToRead(new File(fileName));
            if (m != null) {
                msg = String.format(m, fileName);
            }
        }
        return msg;
    }

    private static String checkFileToRead(@NonNull File f) {
        String msg = null;
        if (!f.exists()) {
            msg = "le fichier %s n'existe pas";
        } else if (!f.isFile()) {
            msg = "l'élément %s n'est pas un fichier valide";
        } else if (!f.canRead()) {
            msg = "le fichier %s ne peut pas être lu";
        }
        return msg;
    }

    private static String checkResultFile(String fileName) {
        String msg;
        if (StringUtils.isEmpty(StringUtils.trim(fileName))) {
            msg = "le nom du fichier résultat n'est pas correctement renseigné";
        } else {
            msg = null;
        }
        return msg;
    }
}
