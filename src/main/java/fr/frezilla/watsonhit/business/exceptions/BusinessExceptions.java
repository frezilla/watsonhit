package fr.frezilla.watsonhit.business.exceptions;

public enum BusinessExceptions {
    fileInNotFound("le fichier d'entrée est introuvable"),
    fileInNotValid("le fichier d'entrée n'est pas un fichier valide");
    
    private final BusinessException e;
    
    private BusinessExceptions(String msg) {
        e =  new BusinessException(msg);
    }
    
    public BusinessException getException() {
        return e;
    }
    
}
