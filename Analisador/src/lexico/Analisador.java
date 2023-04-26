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
				// -----------------------------------//
				case '&': {
					if (proximoChar('&'))
						token = new Token(TipoToken.OPAND, "&&", numeroLinha);
				}
					break;

				case '|': {
					if (proximoChar('|'))
						token = new Token(TipoToken.OPOR, "||", numeroLinha);
				}
					break;
				case '+': {
					token = new Token(TipoToken.OPSUM, "*", numeroLinha);
				}
					break;
				case '-': {
					token = new Token(TipoToken.OPSUB, "-", numeroLinha);
				}
					break;
				case '*': {
					token = new Token(TipoToken.OPMULT, "*", numeroLinha);
				}
					break;
				case '/': {
					token = new Token(TipoToken.OPDIV, "/", numeroLinha);
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
					}
				}
					break;
				default: {
					if (c == 0)
						token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
					else{ 
						if(Character.isLetter(c) || (c == '@' && Character.isLetter(ReturnProximoChar()))) {
                			automato = Automato.IDENTIFICADOR;
                			lexema +=c;
                		}else {
                			automato = Automato.DIGITO;
                			lexema +=c;
                		}
					}
				}
					break;
				}
				break;// switch caracter
			case IDENTIFICADOR:
				c = getChar();
				if (Simbolos.verificaSimbolo(c)) {// verifica se é um lexema já pronto
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
						case "if": {
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
						default: {
							if (c == 0) {
								token = new Token(TipoToken.EOF, "erro fim do arquivo", numeroLinha);
							}else {
									if(lexema.charAt(0) == '@') {
										token = new Token(TipoToken.IDVAR, lexema, numeroLinha);	
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
					if (Simbolos.verificaSimbolo(c)) {
						if(Integer.valueOf(lexema) != null) {
							token = new Token(TipoToken.VALNUM, lexema, numeroLinha);
						}
					}
					else {
						if (Character.isDigit(c))
							lexema += c;
					}
					}
				
			}
				break;
				
				
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
