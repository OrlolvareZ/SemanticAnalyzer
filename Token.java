public class Token {
    
    private String lexema;
    private int token;
    private int posicionTabla;
    private int numLinea;   

    public Token() {
    }

    public Token(int token, String lexema,  int posicionTabla, int numLinea) {
        this.lexema = lexema;
        this.token = token;
        this.posicionTabla = posicionTabla;
        this.numLinea = numLinea;
    }

    public String getLexema() {
        return lexema;
    }

    public int getToken() {
        return token;
    }

    public int getPosicionTabla() {
        return posicionTabla;
    }

    public int getNumLinea() {
        return numLinea;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setPosicionTabla(int posicionTabla) {
        this.posicionTabla = posicionTabla;
    }

    public void setNumLinea(int numLinea) {
        this.numLinea = numLinea;
    }

    public boolean isIdentificador() {
        return posicionTabla == -2  ;
    }

    @Override
    public String toString() {

        return "Token{"
                + "lexema=" + lexema
                + ", token=" + token
                + ", posicionTabla=" + posicionTabla
                + ", # Linea=" + numLinea
                + '}'
                + "\n"
        ;

    }
}
