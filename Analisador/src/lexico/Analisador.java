package lexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * Este analisador faz uso de elementos do projeto de arthurmteodoro (ano 2017)
 * e notas de aula do Prof. Andrei Formiga (ano 2010)
 *
 */
public class Analisador {

	private BufferedReader arquivo;
	private String arquivoNome;
	private String linha;
	private int numeroLinha;
	private int posicaoLinha;

	public void abreArquivo(String nomeArquivo) throws IOException {
		arquivo = new BufferedReader(new FileReader(nomeArquivo));
		arquivoNome = nomeArquivo;
		linha = arquivo.readLine().concat("\n");
		numeroLinha = 1;
		posicaoLinha = 0;
	}

	public void fechaArquivo(String nomeArquivo) throws IOException {
		arquivo.close();
		linha = "";
		this.numeroLinha = 0;
		this.posicaoLinha = 0;
	}

	public char getChar() {
		if (linha == null)
			return 0;// caso a primeira linha lida ao abrir o arquivo seja nula
		if (posicaoLinha == linha.length()) {// se já leu todos os caracteres
			try {
				linha = arquivo.readLine();// busca-se uma nova linha
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (linha == null)// final do arquivo
			{
				numeroLinha++;
				return 0;
			} else {// tem linha nova
				linha = linha.concat("\n");
				numeroLinha++;
				posicaoLinha = 0;
			}
		}
		char ch = linha.charAt(posicaoLinha);// ler o primeiro caracter
		posicaoLinha++;// incrementa a posição
		return ch;// retorna esse caracter
	}

	public boolean proximoChar(char c) {// compara um caracter recebido com o pr�ximo a ser lido
		char proximo = getChar();
		return c == proximo;
	}
	
	public char ReturnProximoChar() {// compara um caracter recebido com o pr�ximo a ser lido
		char proximo =  getChar();
		posicaoLinha--;
		return proximo;
	}
	
	public void ReturnGetChar() {// retorna posição da linha
		posicaoLinha--;
	}
	
	public boolean IsChar(String str) {
		if (str.length() == 1) {
			if (Character.isLetterOrDigit(str.charAt(0)))
				return true;
		}
		
		return false;
	}
	
	public boolean IsFloat(String str) {
		try {
		    Float.parseFloat(str);
		    
		    return true;
		    
		} catch(NumberFormatException e) {
		    
		   return false;
		}
	}

	public Token capturaToken() throws Exception {
		Token token = null;
		Automato automato = Automato.OPERADOR;
		String lexema = "";
		char c;
		while (token == null)// o processo garante o encontro de um token, nem que este seja EOF
		{
			switch (automato) {
			case OPERADOR:
				c = getChar();
				switch (c) {
				case '>': {
					if (proximoChar('='))
						token = new Token(TipoToken.OPMAIORIGUAL, ">=", numeroLinha);
					else {
						posicaoLinha--;// retorna o caracter por conta do metodo proximo char
						token = new Token(TipoToken.OPMAIOR, ">", numeroLinha);
					}
				}
					break;
				case '<': {
					if (proximoChar('='))
						token = new Token(TipoToken.OPMENORIGUAL, "<=", numeroLinha);
					else {
						posicaoLinha--;// retornar um caracter de leitura por conta do metodo proximo char
						token = new Token(TipoToken.OPMENOR, "<", numeroLinha);
					}
				}
					break;
				case '=': {
					token = new Token(TipoToken.OPIGUAL, "=", numeroLinha);
				}
					break;
					
				case '&': {
					if (proximoChar('&'))
						token = new Token(TipoToken.OPAND, "&&", numeroLinha);
					else {
						posicaoLinha--;
						token = new Token(TipoToken.SIMBOLO, "&", numeroLinha);
					}
				}
					break;

				case '|': {
					if (proximoChar('|'))
						token = new Token(TipoToken.OPOR, "||", numeroLinha);
					else {
						posicaoLinha--;
						token = new Token(TipoToken.SIMBOLO, "|", numeroLinha);
					}
				}
					break;
				case '+': {
					token = new Token(TipoToken.OPSUM, "+", numeroLinha);
				}
					break;
				case '-': {
					token = new Token(TipoToken.OPSUB, "-", numeroLinha);
				}
					break;
				case '*': {
					if (proximoChar('/') && !lexema.contains("/*")){
						throw new Exception("Erro na linha" + numeroLinha);
					}
					else {
						posicaoLinha--;
						token = new Token(TipoToken.OPMULT, "*", numeroLinha);
					}
				}
					break;
				case '/': {
					if (proximoChar('*')){
						//throw new Exception("Erro na linha" + numeroLinha);
						automato = Automato.COMENTARIO;
						lexema += "/*";
					}
					else {
						posicaoLinha--;
						token = new Token(TipoToken.OPDIV, "/", numeroLinha);
					}
				}
					break;
				case '%': {
					token = new Token(TipoToken.OPMOD, "%", numeroLinha);
				}
					break;
					
				case ':': {
					if (proximoChar('='))
						token = new Token(TipoToken.CMDATR, ":=", numeroLinha);
					else {
						posicaoLinha--;
						token = new Token(TipoToken.SIMBOLO, ":", numeroLinha);
					}
				}
					break;
				
				case '[': {
					token = new Token(TipoToken.SIMBOLO, "[", numeroLinha);
				}
					break;
					
				case ']': {
					token = new Token(TipoToken.SIMBOLO, "]", numeroLinha);
				}
					break;
				
				case '#': {
					token = new Token(TipoToken.SIMBOLO, "#", numeroLinha);
				}
					break;
				case '.': {
					token = new Token(TipoToken.SIMBOLO, ".", numeroLinha);
				}
					break;
				case ',': {
					token = new Token(TipoToken.SIMBOLO, ",", numeroLinha);
				}
					break;
				case ';': {
					token = new Token(TipoToken.IDTERMINADOR, ";", numeroLinha);
				}
					break;
				case '$': {
					token = new Token(TipoToken.SIMBOLO, "$", numeroLinha);
				}
					break;
				case '(': {
					token = new Token(TipoToken.SIMBOLO, "(", numeroLinha);
				}
					break;
				case ')': {
					token = new Token(TipoToken.SIMBOLO, ")", numeroLinha);
				}
					break;
				case '{': {
					token = new Token(TipoToken.SIMBOLO, "{", numeroLinha);
				}
					break;
				case '}': {
					token = new Token(TipoToken.SIMBOLO, "}", numeroLinha);
				}
					break;
				case '~': {
					token = new Token(TipoToken.SIMBOLO, "~", numeroLinha);
				}
					break;
				case '^': {
					token = new Token(TipoToken.SIMBOLO, "^", numeroLinha);
				}
					break;
				case '`': {
					token = new Token(TipoToken.SIMBOLO, "`", numeroLinha);
				}
					break;
				
				case 'º': {
					token = new Token(TipoToken.SIMBOLO, "º", numeroLinha);
				}
					break;
				case '!': {
					token = new Token(TipoToken.OPNEGACAO, "!", numeroLinha);
				}
					break;
				case '?': {
					token = new Token(TipoToken.SIMBOLO, "?", numeroLinha);
				}
					break;
				default: {
					if (c == 0)
						token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
					else{
						if(Character.isLetter(c) || (c == '@' && Character.isLetter(ReturnProximoChar()))) {
                			automato = Automato.IDENTIFICADOR;
                			lexema +=c;
                		}else if(c == '@' && ReturnProximoChar() == ' ') {
                			token = new Token(TipoToken.SIMBOLO, "@", numeroLinha);
                		
                		}else if (c == "'".charAt(0)) {
                			
                			automato = Automato.STRING;
                			lexema +=c;
                			
                		}else if(Character.isDigit(c))
                		{
                			if(Simbolos.verificaSimbolo(ReturnProximoChar()))
                			{
                				lexema += c;
                				token = new Token(TipoToken.VALNUM, lexema, numeroLinha);
                			}
                			else {
                				automato = Automato.DIGITO;
                    		 	lexema +=c;
                			}
                		}
                		else 
                		 {	
                			if(!Simbolos.verificaSimbolo(c))
                				throw new Exception("Erro na linha" + numeroLinha);
						 }
					}
				}
					break;
				}
				break;// switch caracter
			case IDENTIFICADOR:
				
				c = getChar();
				
				if(c == ';')
				{
					automato = Automato.OPERADOR;
    				ReturnGetChar();
				}
				
				if (Simbolos.verificaSimbolo(ReturnProximoChar()) || Simbolos.verificaSimbolo(c)) {// verifica se é um lexema já pronto
				
					if(!Simbolos.verificaSimbolo(c))
						lexema += c;
					
					switch (lexema) {
						case "int": {
							token = new Token(TipoToken.IDTIPO, lexema, numeroLinha);
						}
							break;
						case "float": {
							token = new Token(TipoToken.IDTIPO, lexema, numeroLinha);
						}
							break;
						case "bool": {
							token = new Token(TipoToken.IDTIPO, lexema, numeroLinha);
						}
							break;
						case "char": {
							token = new Token(TipoToken.IDTIPO, lexema, numeroLinha);
						}
							break;
						case "string": {
							token = new Token(TipoToken.IDTIPO, lexema, numeroLinha);
						}
							break;
							
						case "CONST": {
							token = new Token(TipoToken.IDCONTANTE, lexema, numeroLinha);
						}
						
							break;
							
						case "if": {
							token = new Token(TipoToken.CMDIF, lexema, numeroLinha);
						}
							break;
							
						case "else": {
							token = new Token(TipoToken.CMDIF, lexema, numeroLinha);
						}
							break;
						case "while": {
							token = new Token(TipoToken.CMDWHILE, lexema, numeroLinha);
						}
							break;
						
						case "true": {
							token = new Token(TipoToken.VALBOOL, lexema, numeroLinha);
						}
							break;
							
						case "false": {
							token = new Token(TipoToken.VALBOOL, lexema, numeroLinha);
						}
							break;
							
						case "static": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
							break;
							
						case "void": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
						
							break;
							
						case "Main": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
						
							break;
							
						case "then": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
						
							break;
							
						case "do": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
						
							break;
							
						case "end": {
							token = new Token(TipoToken.PALAVRA_CHAVE, lexema, numeroLinha);
						}
						
							break;
						default: {
							if (c == 0) {
								token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
							}else {
									if(lexema.charAt(0) == '@') {
										token = new Token(TipoToken.IDVAR, lexema, numeroLinha);	
									} else if(IsChar(lexema)) {
										token = new Token(TipoToken.VAL_CHAR, lexema, numeroLinha);	
									}
									else {
										throw new Exception("Erro na linha" + numeroLinha);
									}
							}
						}
							break;
					}// switch lexema
				} // if
				else {
					if (Character.isLetterOrDigit(c))
						lexema += c;
				}
				break;
				
			case DIGITO:
			{
				c = getChar();
				
				if (c == 0) {
					token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
				}else {
					if (Simbolos.verificaSimbolo(ReturnProximoChar()) || Simbolos.verificaSimbolo(c)) {
						
						if(!Simbolos.verificaSimbolo(c))
							lexema += c;
						
						if(IsFloat(lexema)) {
							
							if(lexema.contains("."))
								token = new Token(TipoToken.VALFLOAT, lexema, numeroLinha);
							else
								token = new Token(TipoToken.VALNUM, lexema, numeroLinha);
						}
						else {
							throw new Exception("Erro na linha" + numeroLinha);
						}
					}
					else {
						if ((Character.isDigit(c)) || (c == '.'))
							lexema += c;
						else
							throw new Exception("Erro na linha " + numeroLinha);
					}
				}
				
			}
				break;
			
			case COMENTARIO:{
				c = getChar();
				
			
				if (c == 0) {
					if(lexema.contains("/*"))
						throw new Exception("Erro na linha " + --numeroLinha);
					else
						token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
				}else {
					if (c == '*' && ReturnProximoChar() == '/'){
					
						if(ReturnProximoChar() == ';')
						{
							automato = Automato.OPERADOR;
		    				ReturnGetChar();
						}
						
						
						posicaoLinha++;
						lexema += "*/";
						token = new Token(TipoToken.COMENT, lexema, numeroLinha);	
					}
					
					else
						lexema += c;
				}
			}
				break;
			
				
			case STRING:{
				c = getChar();
				
				if (c == 0) {
					if(lexema.contains("'"))
						throw new Exception("Erro na linha " + --numeroLinha);
					else
						token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
				}else {
					if (c == "'".charAt(0)){
						
						if(ReturnProximoChar() == ';')
						{
							automato = Automato.OPERADOR;
		    				ReturnGetChar();
						}
						
						posicaoLinha++;
						lexema += c;
						token = new Token(TipoToken.VAL_STRING, lexema, numeroLinha);	
						
						
					}
					
					else
						lexema += c;
				}
				
				
			}
				break;
				
			default:
				throw new Exception("Erro na linha " + numeroLinha);
				
			}// switch aut�mato 
		} // while
		return token;
	}

	public static void main(String[] args) {
		Analisador lexico = new Analisador();
		Token token;
		try {
			lexico.abreArquivo("teste.txt");
			token = lexico.capturaToken();
			while (token.getToken() != TipoToken.EOF) {
				System.out.println(token.toString());
				token = lexico.capturaToken();
			}
			System.out.println(token.toString());
			lexico.fechaArquivo("teste.txt");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
}
