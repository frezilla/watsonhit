package fr.frezilla.watsonhit;

enum BusinessExceptions {
    argumentsError("les arguments passés en ligne de commande ne peuvent pas être lues"),
    csvDescriptionError("erreur au traitement de la description du fichier csv"),
    csvDescriptionNotValid("la description du fichier csv n'est pas valide : <%s>"),
    csvFileError("erreur au traitement du fichier csv"),
    csvFileFormatError("le format du fichier csv n'est pas conforme à la description chargée (ligne %d - nombre de colonnes attendues : %d ; nombre de colonnes lues : %d"),
    fileInNotFound("le fichier d'entrée est introuvable"),
    fileInNotValid("le fichier d'entrée n'est pas un fichier valide"),
    parametersError("les paramètres d'entrée du traitement ne sont pas valides \n%s"),
    resultFileError("le fichier de résultat %s ne peut pas être créé");

    private final String businessExceptionMsg;

    private BusinessExceptions(String msg) {
        businessExceptionMsg = msg;
    }

    public BusinessException build() {
        return new BusinessException(businessExceptionMsg);
    }

    public BusinessException build(Object... args) {
        return new BusinessException(String.format(businessExceptionMsg, args));
    }
}
