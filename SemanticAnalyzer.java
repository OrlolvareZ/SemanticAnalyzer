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
import javax.swing.JOptionPane;

/*
 * This class is responsible for analyzing the semantics of the code
 * It checks for the following:
 * - If the variable has been declared
 * - If the data type of the variable matches the data type of the token
 * - If the assignment is valid
 */

public class SemanticAnalyzer {

    private static List<Token>     tokens = new ArrayList<Token>();
    private static List<Token>     tokensInVarSection = new ArrayList<Token>();
    private static List<Token>     tokensInBodySection = new ArrayList<Token>();
    private static List<String>    symbolsTable = new ArrayList<String>();
    private static List<String>    addressesTable = new ArrayList<String>();
    private static Set<String>          variables = new HashSet<String>();

    public static void main(String[] args) {

        try {

            // Create directory for temp files
            File tablesDir = new File("tables");
            if(!tablesDir.exists()){
                tablesDir.mkdir();
            }
            // Load temp files
            File symbolsTableFile = new File(tablesDir, "symbolTable.txt");
            File addressesTableFile = new File(tablesDir, "addressesTable.txt");
            File tokensTableFile = new File(tablesDir, "tokensTable.txt");

            if(symbolsTableFile.exists() && addressesTableFile.exists() && tokensTableFile.exists()){
                symbolsTableFile.delete();
                addressesTableFile.delete();
                tokensTableFile.delete();
            }

            // Read tokens from file
            BufferedReader br = new BufferedReader(new FileReader("tokens.txt"));
            String fileLine;        

            // Reads as it checks for the end of the file
            while((fileLine = br.readLine()) != null) {

                String[] tokenInfo = fileLine.split(",");

                for (int i = 0; i < tokenInfo.length; i++) {
                    tokenInfo[i] = tokenInfo[i].trim();
                }

                // Commas are the delimiter for the tokens table.
                // When the value for the lexeme is empty, asume it was a comma
                // causing a parsing error
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

            // Split tokens into declaration and body sections
            splitSections();
            
            // Aux index for looking at tokens ahead
            int index = -1;

            Map<String, Integer> tablePositions = new HashMap<String, Integer>();

            for(Token token : tokensInVarSection){

                index++;

                if(!token.isIdentifier())
                {
                    continue;
                }

                if(token.getToken() == Syntax.GENERAL_ID)
                {
                    if(!variables.contains(token.getLexema()))
                    {
                        variables.add(token.getLexema());

                        addRowToAddressesTable(token.getLexema(), token.getToken(), token.getNumLinea(), 0);
                        token.setPosicionTabla(0);
                    }
                }
                // ... if not, it's a typed identifier, which is a variable
                else
                {
                    if(variables.contains(token.getLexema())){
                        JOptionPane.showMessageDialog(null, "Error semántico: El identificador " + token.getLexema() + " ya ha sido declarado");
                        System.exit(0);
                    }

                    if(!checkDataTypes(token.getToken(), index)){
                        JOptionPane.showMessageDialog(null, "Error semántico: El tipo de dato no coincide con el tipo de dato de la variable");
                        System.exit(0);
                    }

                    // Keep track of the newly found identifier
                    variables.add(token.getLexema());

                    addRowToSymbolsTable(token.getLexema(), token.getToken(), getIdentifierDefaultValue(token.getToken()), "Main", variables.size() - 1);
                    
                    tablePositions.put(token.getLexema(), variables.size()-1);
                    
                    token.setPosicionTabla(variables.size()-1);
                    
                }
                
            }

            for(Token token : tokensInBodySection){

                index++;

                if(!token.isIdentifier())
                {
                    continue;
                }

                if(!variables.contains(token.getLexema()))
                {
                    JOptionPane.showMessageDialog(null, "Error semántico: El identificador " + token.getLexema() + " no ha sido declarado");
                    System.exit(0);
                }

                else
                {
                    // For known identifiers, set their known position in the table
                    tokens.get(index).setPosicionTabla(tablePositions.get(token.getLexema()));

                    // Handle assignments
                    if(tokens.get(index+1).getToken() == Syntax.ASSIGN_OP){

                        if(!assignmentIsValid(token.getToken(), index)){
                            JOptionPane.showMessageDialog(null, "Error semántico: La asignación no es válida");
                            System.exit(0);
                        }
                    }

                    // Handle blocks
                    int blockToken = tokens.get(index-2).getToken();
                    if(
                        blockToken == Syntax.IF
                        || blockToken == Syntax.WHILE
                        || blockToken == Syntax.UNTIL
                    ){
                        if(!booleanExpressionIsValid(token.getToken(), index, blockToken)){
                            JOptionPane.showMessageDialog(null, "Error semántico: La condición no es válida");
                            System.exit(0);
                        }
                    }
                }
            }

            // All checks passed 🥳

            JOptionPane.showMessageDialog(null, "Análisis semántico completado");
            
            writeTableToFile(SemanticAnalyzer.symbolsTable, symbolsTableFile);
            writeTableToFile(SemanticAnalyzer.addressesTable, addressesTableFile);
            writeTableToFile(buildTextTokensTable(tokens), tokensTableFile);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + e.getMessage());
        }

    }

    
    public static boolean booleanExpressionIsValid(int token, int index, int structure){

        List<Integer> foundTokens = new ArrayList<Integer>();

        if(structure == Syntax.IF || structure == Syntax.WHILE){

            for(int i = index; i < tokens.size(); i++) {

                foundTokens.add(tokens.get(i).getToken());

                // The boolean expression ends when we find the pattern:
                // ...) { entonces inicio... <-
                if(tokens.get(i+3).getToken() == Syntax.BEGIN){
                    return Syntax.logicalAndRelationalOperators.stream().anyMatch(foundTokens::contains);
                }

            }

        }
        else { // structure == Syntax.UNTIL

            for(int i = index; i < tokens.size(); i++){

                foundTokens.add(tokens.get(i).getToken());

                // The boolean expression ends when we find the pattern:
                // ...}
                // hasta (
                //  ...
                // ); <-
                if(tokens.get(i+2).getToken() == Syntax.SEMICOLON){
                    return Syntax.logicalAndRelationalOperators.stream().anyMatch(foundTokens::contains);
                }

            }

        }

        return false; 
    }
    
    /**
     * This method checks if the assignment is valid
     * @param token The token to be evaluated
     * @param index The index of the token
     * @return True if the assignment is valid, false if it's not
     */
    public static boolean assignmentIsValid(int token, int index){

        Set<Integer> conditionCheckAssignmentEntero = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -52, -53, -54, -55, -62, -63, -64, -65)
        ); // Allow only assignment of integer data types -> int id and int constant
        Set<Integer> conditionCheckAssignmentReal = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -53, -54, -55, -63, -64, -65)
        ); // -> int id, real id, int constant, real constant
        Set<Integer> conditionCheckAssignmentCadena = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -51, -52, -54, -55, -61, -62, -64, -65)
        );
        Set<Integer> conditionCheckAssignmentLogico = new HashSet<Integer>(
            Arrays.asList(-31, -32, -33, -34, -35, -36, -41, -42, -43, -64, -65)
        );

        List<Integer> foundTokens = new ArrayList<Integer>();

        for(Token _token : tokens){

            foundTokens.add(_token.getToken());

            // Upon the end of the expression, check if all the tokens (ope)
            if(_token.getToken() == Syntax.SEMICOLON){
                
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
     * This method checks if the data types of the tokens match with the data type
     * announced at the end of the expression where they are declared
     * 
     * @param token The token to be evaluated, which is an identifier
     * @param index The index of the token
     * @return True if the typed id matches the data type announced at the end
     *          of its declaration, false if it doesn't
     */
    public static boolean checkDataTypes(int token, int index){

        for(int i = index; i < tokensInVarSection.size(); i++){

            // Go through the tokens in the var section until the end of expression
            if(tokensInVarSection.get(i).getToken() == Syntax.SEMICOLON){

                // Check the data type announced at the end of the expression...
                String announcedDataType = tokensInVarSection.get(i-1).getLexema();
                // ... and compare it to the actual data type of the token,
                // which is an identifier
                return announcedDataType.equals(getAllowedDataType(token));

            }

        }

        return false;

    }

    /**
     * This method assigns found tokens to their respective sections
     */
    public static void splitSections(){

        List<Token> currentTargetSection = tokensInVarSection;

        for(Token token : tokens){

            // If the token is the beginning of the body section ("inicio")
            // then register tokens in the body section
            if(token.getToken() == Syntax.BEGIN){
                currentTargetSection = tokensInBodySection;
            }

            currentTargetSection.add(token);
        }

    }

    /**
     * This method returns the data type of an identifier token
     * 
     * @param token The token to be evaluated
     * @return The data type of the token
     */
    public static String getAllowedDataType(int token){

        if (token == Syntax.INT_ID) {
            return "entero";
        } else if (token == Syntax.REAL_ID) {
            return "real";
        } else if (token == Syntax.STRING_ID) {
            return "cadena";
        } else if (token == Syntax.BOOLEAN_ID) {
            return "logico";
        } else {
            return "null";
        }

    }

    /**
     * This method returns the default value of an identifier token
     * 
     * @param tokenId The token to be evaluated
     * @return The value of the token
     */
    public static String getIdentifierDefaultValue(int tokenId){
        
        if (tokenId == Syntax.INT_ID) {
            return "0";
        } else if (tokenId == Syntax.REAL_ID) {
            return "0.0";
        } else if (tokenId == Syntax.STRING_ID) {
            return "null";
        } else if (tokenId == Syntax.BOOLEAN_ID) {
            return "true";
        } else {
            return "null";
        }

    }

    /**
     * This method adds a row to the symbols table
     * @param lexema The lexema to be added
     * @param token The token to be added
     * @param value The value to be added
     * @param ambit The ambit to be added
     * @param tableNumber The table number to be added
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
     * This method adds a row to the addresses table
     * @param lexema The lexema to be added
     * @param token The token to be added
     * @param numLine The number of the line to be added
     * @param vci The VCI to be added
     */
    public static void addRowToAddressesTable(
        String lexema,
        int token,
        int numLine,
        int vci
    ){
        addressesTable.add(token + " , " + lexema + " , " + numLine + " , " + vci);
    }
    
    /**
     * This method creates a table
     * @param table The table to be created
     * @param name The name of the table
     * @param message The message to be displayed
     */
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
     * This method creates a table
     * @param table The table to be created
     * @param name The name of the table
     * @param message The message to be displayed
     */
    public static List<String> buildTextTokensTable(List<Token> tokens){

        ArrayList<String> table = new ArrayList<String>();
        for(Token token : tokens){
            table.add(token.getToken() + " , " + token.getLexema() + " , " + token.getPosicionTabla() + " , " + token.getNumLinea());
        }
        return table;

    }
}