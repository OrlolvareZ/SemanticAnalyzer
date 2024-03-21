import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JOptionPane;

/*
 * Esta clase analiza los tokens generados por el analizador léxico
 * (procesados además por el analizador sintáctico) y verifica que:
 * - Los identificadores estén declarados apropiadamente
 * - Los identificadores con tipo asignado se anuncien bajo el tipo
 *  de dato correcto
 * - Las asignaciones sean válidas
 * - Las condiciones en estatutos de control sean válidas
 * 
 * Autores:
 * - Orlando Miguel Alvarez Alfaro
 * - Arturo
 * - Julio
 */

public class AnalizadorSemantico {

    private static List<Token>     tokens = new ArrayList<Token>();
    private static List<Token>     tokensInVarSection = new ArrayList<Token>();
    private static List<Token>     tokensInBodySection = new ArrayList<Token>();
    private static List<String>    symbolsTable = new ArrayList<String>();
    private static List<String>    addressesTable = new ArrayList<String>();
    private static Set<String>          variables = new HashSet<String>();
    private static Stack<String>        ambits = new Stack<String>();

    public static void main(String[] args) {

        try {

            // Creación de directorio y archivos para las tablas
            File tablesDir = new File("tables");
            if(!tablesDir.exists()){
                tablesDir.mkdir();
            }
            File symbolsTableFile = new File(tablesDir, "symbolTable.txt");
            File addressesTableFile = new File(tablesDir, "addressesTable.txt");
            File tokensTableFile = new File(tablesDir, "tokensTable.txt");

            if(symbolsTableFile.exists() && addressesTableFile.exists() && tokensTableFile.exists()){
                symbolsTableFile.delete();
                addressesTableFile.delete();
                tokensTableFile.delete();
            }

            // Lectura del archivo de tokens
            BufferedReader br = new BufferedReader(new FileReader("tokens.txt"));
            String fileLine;        

            while((fileLine = br.readLine()) != null) {

                String[] tokenInfo = fileLine.split(",");

                for (int i = 0; i < tokenInfo.length; i++) {
                    tokenInfo[i] = tokenInfo[i].trim();
                }

                // Las comas son el delimitador de los tokens.
                // Cuando una es el lexema, causa un error en el parsing
                // de los tokens. En el caso en que el lexema está vacío,
                // sabemos que la coma es el lexema y ajustamos
                if(tokenInfo[1].equals("")){
                    String[] temp = new String[4];
                    temp[0] = tokenInfo[0];
                    temp[1] = ",";
                    temp[2] = tokenInfo[3];
                    temp[3] = tokenInfo[4];
                    tokenInfo = temp;
                }

                tokens.add(
                    new Token(
                        Integer.parseInt(tokenInfo[0]),
                        tokenInfo[1],
                        Integer.parseInt(tokenInfo[2]),
                        Integer.parseInt(tokenInfo[3].trim())
                    )
                );

            }

            br.close();

            // Asignar tokens a sus respectivas secciones
            splitSections();
            
            // Índice auxiliar para la navegación de la tabla de tokens
            int index = -1;

            Map<String, Integer> tablePositions = new HashMap<String, Integer>();

            // Para los tokens en la sección de variables,
            // revisamos que los identificadores estén declarados apropiadamente
            // y que los identificadores con tipo asignado se anuncia bajo el tipo
            // de dato correcto
            for(Token token : tokensInVarSection){

                index++;

                if(!token.isIdentificador())
                {
                    continue;
                }

                // Identificador de programa
                if(token.getToken() == Sintaxis.GENERAL_ID)
                {
                    // Guardamos el ámbito actual
                    ambits.push(token.getLexema());

                    // Si no están, los agregamos a la tabla de direcciones
                    if(!variables.contains(token.getLexema()))
                    {
                        variables.add(token.getLexema());

                        addRowToAddressesTable(token.getLexema(), token.getToken(), token.getNumLinea(), 0);
                        token.setPosicionTabla(0);
                    }
                }
                // ... Identificadores de variables
                else
                {
                    // Si el identificador ya ha sido declarado, terminamos el programa
                    if(variables.contains(token.getLexema())){
                        JOptionPane.showMessageDialog(null, "Error semántico: El identificador " + token.getLexema() + " ya ha sido declarado");
                        System.exit(0);
                    }

                    // Si el tipo de dato anunciado no coincide con el tipo de dato de
                    // que anuncia el identifcador, terminamos el programa
                    if(!checkDataTypes(token.getToken(), index)){
                        JOptionPane.showMessageDialog(null, "Error semántico: El tipo de dato no coincide con el tipo de dato de la variable");
                        System.exit(0);
                    }


                    variables.add(token.getLexema());

                    addRowToSymbolsTable(
                        token.getLexema(),
                        token.getToken(),
                        getIdentifierDefaultValue(token.getToken()),
                        ambits.peek(),
                        variables.size() - 1
                    );
                    
                    tablePositions.put(token.getLexema(), variables.size()-1);
                    
                    token.setPosicionTabla(variables.size()-1);
                    
                }
                
            }

            for(Token token : tokensInBodySection){

                index++;

                if(!token.isIdentificador())
                {
                    continue;
                }

                if(!variables.contains(token.getLexema()))
                {
                    JOptionPane.showMessageDialog(null, "Error semántico: El identificador " + token.getLexema() + " no ha sido declarado");
                    System.exit(0);
                }


                // Cuando el token corresponde a un identificador, su posición en la tabla
                // de símbolos se recupera
                tokens.get(index).setPosicionTabla(tablePositions.get(token.getLexema()));

                // Revisa la validez de las asignaciones
                if(tokens.get(index+1).getToken() == Sintaxis.ASIGN_OP){

                    if(!assignmentIsValid(token.getToken(), index)){
                        JOptionPane.showMessageDialog(null, "Error semántico: La asignación no es válida");
                        System.exit(0);
                    }
                }

                // Revisa la validez de las condiciones
                int blockToken = tokens.get(index-2).getToken();
                if(
                    blockToken == Sintaxis.IF
                    || blockToken == Sintaxis.MIENTRAS
                    || blockToken == Sintaxis.HASTA
                ){
                    if(!booleanExpressionIsValid(token.getToken(), index, blockToken)){
                        JOptionPane.showMessageDialog(null, "Error semántico: La condición no es válida");
                        System.exit(0);
                    }
                }
            }

            // ¡Se completó el análisis con éxito! 🥳
            JOptionPane.showMessageDialog(null, "Análisis semántico completado");
            
            writeTableToFile(AnalizadorSemantico.symbolsTable, symbolsTableFile);
            writeTableToFile(AnalizadorSemantico.addressesTable, addressesTableFile);
            writeTableToFile(buildTextTokensTable(tokens), tokensTableFile);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + e.getMessage());
        }

    }

    
    public static boolean booleanExpressionIsValid(int token, int index, int structure){

        List<Integer> foundTokens = new ArrayList<Integer>();

        if(structure == Sintaxis.IF || structure == Sintaxis.MIENTRAS){

            for(int i = index; i < tokens.size(); i++) {

                foundTokens.add(tokens.get(i).getToken());

                // La expresión booleana termina al encontrar el patrón:
                // ...) { entonces inicio... <-
                if(tokens.get(i+3).getToken() == Sintaxis.INICIO){
                    return Sintaxis.logicalAndRelationalOperators.stream().anyMatch(foundTokens::contains);
                }

            }

        }
        else { // structure == Syntax.UNTIL

            for(int i = index; i < tokens.size(); i++){

                foundTokens.add(tokens.get(i).getToken());

                // La expresión booleana termina al encontrar el patrón:
                // ...}
                // hasta (
                //  ...
                // ); <-
                if(tokens.get(i+2).getToken() == Sintaxis.PUNTOCOMA){
                    return Sintaxis.logicalAndRelationalOperators.stream().anyMatch(foundTokens::contains);
                }

            }

        }

        return false; 
    }
    
    /**
     * Revisa que la asignación a un identificador sea válida
     * 
     * @param token El token a ser evaluado, que es un identificador
     * @param index El índice del token
     * @return Verdadero si la asignación es válida, falso si no lo es
     */
    public static boolean assignmentIsValid(int token, int index){

        Set<Integer> conditionCheckAssignmentEntero = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -52, -53, -54, -55, -62, -63, -64, -65)
        );
        Set<Integer> conditionCheckAssignmentReal = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -53, -54, -55, -63, -64, -65)
        );
        Set<Integer> conditionCheckAssignmentCadena = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -51, -52, -54, -55, -61, -62, -64, -65)
        );
        Set<Integer> conditionCheckAssignmentLogico = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -64, -65)
        );

        List<Integer> foundTokens = new ArrayList<Integer>();

        for(Token _token : tokens){

            foundTokens.add(_token.getToken());

            // Dado el final de la expresión, revisa que los tokens encontrados
            // generen una expresión del tipo esperado
            if(_token.getToken() == Sintaxis.PUNTOCOMA){
                
                int currentVar = foundTokens.get(0);
                String currentVarDataType = getAllowedDataType(currentVar);
                
                switch(currentVarDataType){
                    case "entero":
                        if(conditionCheckAssignmentEntero.stream().anyMatch(foundTokens::contains)){
                            return false;
                        }
                        break;
                    case "real":
                        if(conditionCheckAssignmentReal.stream().anyMatch(foundTokens::contains)){
                            return false;
                        }
                        break;
                    case "cadena":
                        if(conditionCheckAssignmentCadena.stream().anyMatch(foundTokens::contains)){  
                            return false;
                        }
                        break;
                    case "logico":
                        if(!conditionCheckAssignmentLogico.stream().anyMatch(foundTokens::contains)){
                            return false;
                        }
                        break;
                }

                return true;
            }
        }

        return false;

    }

    /**
     * Revisa que el tipo de dato anunciado al final de la declaración de una
     * variable coincida con el tipo de dato del identificador
     * 
     * @param token El token a ser evaluado, que es un identificador
     * @param index El índice del token
     * @return Verdadero si el tipo de dato anunciado coincide con el tipo de dato del identificador,
     *          falso si no
     */
    public static boolean checkDataTypes(int token, int index){

        for(int i = index; i < tokensInVarSection.size(); i++){

            // Observa los tokens de la declaración hasta finalizar la expresión
            if(tokensInVarSection.get(i).getToken() == Sintaxis.PUNTOCOMA){

                // Revisa el tipo de dato anunciado al final de la declaración...
                String announcedDataType = tokensInVarSection.get(i-1).getLexema();
                // ... y lo compara con el tipo de dato del identificador
                return announcedDataType.equals(getAllowedDataType(token));

            }

        }

        return false;

    }

    /**
     * Este método separa los tokens en las secciones de variables y cuerpo
     */
    public static void splitSections(){

        List<Token> currentTargetSection = tokensInVarSection;

        for(Token token : tokens){

            // Si el token es "inicio", cambiamos el destino de los tokens
            // subsecuentes a la sección de cuerpo
            if(token.getToken() == Sintaxis.INICIO){
                currentTargetSection = tokensInBodySection;
            }

            currentTargetSection.add(token);
        }

    }

    /**
     * Este método devuelve el tipo de dato permitido para un token
     * 
     * @param token El token a ser evaluado
     * @return El tipo de dato permitido para el token
     */
    public static String getAllowedDataType(int token){

        if (token == Sintaxis.INT_ID) {
            return "entero";
        } else if (token == Sintaxis.REAL_ID) {
            return "real";
        } else if (token == Sintaxis.STRING_ID) {
            return "cadena";
        } else if (token == Sintaxis.BOOLEANO_ID) {
            return "logico";
        } else {
            return "null";
        }

    }

    /**
     * Este método devuelve el valor por defecto de un token
     * 
     * @param tokenId El token a ser evaluado
     * @return El valor por defecto del token
     */
    public static String getIdentifierDefaultValue(int tokenId){
        
        if (tokenId == Sintaxis.INT_ID) {
            return "0";
        } else if (tokenId == Sintaxis.REAL_ID) {
            return "0.0";
        } else if (tokenId == Sintaxis.STRING_ID) {
            return "null";
        } else if (tokenId == Sintaxis.BOOLEANO_ID) {
            return "true";
        } else {
            return "null";
        }

    }

    /**
     * Este método agrega una fila a la tabla de símbolos
     * 
     * @param lexema El lexema a ser agregado
     * @param token El token a ser agregado
     * @param value El valor a ser agregado
     * @param ambit El ámbito en el que se encuentra
     * @param tableNumber El número de la tabla a la que se agregará la fila
     */
    public static void addRowToSymbolsTable(
        String lexema,
        int token,
        String value,
        String ambit,
        int tableNumber
    ){
        symbolsTable.add(token + " , " + lexema + " , " + value + " , " + ambit);
    }

    /**
     * Este método agrega una fila a la tabla de direcciones
     * 
     * @param lexema El lexema a ser agregado
     * @param token El token a ser agregado
     * @param numLine El número de línea a ser agregado
     * @param vci El valor de control de la instrucción a ser agregado
     */
    public static void addRowToAddressesTable(
        String lexema,
        int token,
        int numLine,
        int vci
    ){
        addressesTable.add(token + " , " + lexema + " , " + numLine + " , " + vci);
    }
    
    public static void writeTableToFile(List<String> table, File file){

        try {

            FileWriter fw = new FileWriter(file);
            for(String str : table){
                fw.write(str + System.lineSeparator());
            }
            fw.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo: " + e.getMessage());
        }
        
    }

    /**
     * Formatea la tabla de tokens en una lista de cadenas
     * 
     * @param tokens La lista de tokens a ser formateada
     */
    public static List<String> buildTextTokensTable(List<Token> tokens){

        ArrayList<String> table = new ArrayList<String>();
        for(Token token : tokens){
            table.add(token.getToken() + " , " + token.getLexema() + " , " + token.getPosicionTabla() + " , " + token.getNumLinea());
        }
        return table;

    }
}