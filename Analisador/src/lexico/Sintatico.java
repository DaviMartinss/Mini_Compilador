package lexico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexico.*;

public class Sintatico {
	
	public Token lookahed;
	public Analisador lexico;
	
	public Sintatico(String arquivo) throws IOException {
		lexico = new Analisador();
		lexico.abreArquivo(arquivo);
	}
	
	public void analise() throws Exception {
		
		lookahed = lexico.capturaToken();
		
		Programa();
		
		consumir(TipoToken.EOF);
		
		System.out.println("Análise Finalizada");
	}
	
	//Escopo GLOBAL PROGRAMA
	private void Programa() throws Exception {
		//static void Main (string[] @args){
		consumirLexema(TipoToken.PALAVRA_CHAVE, "static" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "void" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "Main" );
		consumirLexema(TipoToken.SIMBOLO, "(" );
		consumirLexema(TipoToken.IDTIPO, "string" );
		consumirLexema(TipoToken.SIMBOLO, "[" );
		consumirLexema(TipoToken.SIMBOLO, "]" );
		consumir(TipoToken.IDVAR);
		consumirLexema(TipoToken.SIMBOLO, ")" );
		consumirLexema(TipoToken.SIMBOLO, "{" );
		
		while (lookahed.getLexema() != "}") {
			
			if(lookahed.getToken() == TipoToken.IDTIPO || lookahed.getToken() == TipoToken.IDCONTANTE) {
				EscopoDeclaracao();
				
			}else if((lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("if")) ||
					  (lookahed.getToken() == TipoToken.CMDWHILE && lookahed.getLexema().equals("while")) ||
					  (lookahed.getToken() == TipoToken.IDVAR) ||
					  (lookahed.getToken() == TipoToken.IDCONTANTE) || 
					  (lookahed.getToken() == TipoToken.IDTIPO))
			{
				ConsumirComandos();
			}
			else if(lookahed.getToken() == TipoToken.COMENT) {
				lookahed = lexico.capturaToken();
				
			}else {
				ErroInesperadoSintatico();
				
			}
			
		}
		
		
		consumirLexema(TipoToken.SIMBOLO, "}" );

		
	}
	
	//Escopo geral da Declaração de variáveis ou Constantes
	private void EscopoDeclaracao() throws Exception {
		
		while (lookahed.getToken() == TipoToken.IDTIPO || lookahed.getToken() == TipoToken.IDCONTANTE)
		{
			if(lookahed.getToken() == TipoToken.IDTIPO) {
				consumirDeclaracao();
				
			}else 
			{
				consumirConstante();
			}
		}
	}
	
	//Escopo geral do IF
	private void EscopoCondicional() throws Exception {
		consumir(TipoToken.CMDIF);
		consumirLexema(TipoToken.SIMBOLO, "(" );
		
		if(lookahed.getToken() == TipoToken.OPNEGACAO)
			ConsumirNegacao();
		else
			consumirExpBool();
		
		consumirLexema(TipoToken.SIMBOLO, ")" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "then" );
		
		ConsumirComandos();
		
		if(lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("else")) {
			ConsumirElse();
			
		}else if(lookahed.getToken() == TipoToken.PALAVRA_CHAVE && lookahed.getLexema().equals("end")) {
			consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
			
		}else {
			ErroInesperadoSintatico();
		}
	}

	//Escopo geral do WHILE
	private void EscopoRepeticao() throws Exception {

		consumir(TipoToken.CMDWHILE);
		consumirLexema(TipoToken.SIMBOLO, "(" );
		
		if(lookahed.getToken() == TipoToken.OPNEGACAO)
			ConsumirNegacao();
		else
			consumirExpBool();
		
		consumirLexema(TipoToken.SIMBOLO, ")" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "do" );
		ConsumirComandos();
		consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
		
	}

	private void consumirConstante() throws Exception {
		 
		consumir(TipoToken.IDCONTANTE);
		consumir(TipoToken.IDTIPO);
		consumir(TipoToken.IDVAR);
		consumir(TipoToken.CMDATR);
		consumirValorUnico();
		consumir(TipoToken.IDTERMINADOR);
	}
	
	private void consumirDeclaracao() throws Exception {

		consumir(TipoToken.IDTIPO);
		consumirItemDeclaracao();

		while (lookahed.getToken() != TipoToken.IDTERMINADOR) {
			consumirLexema(TipoToken.SIMBOLO, ",");
			consumirItemDeclaracao();
		}

		consumir(TipoToken.IDTERMINADOR);
	}
	
	private void consumirItemDeclaracao() throws Exception {
		
		consumir(TipoToken.IDVAR);
		
		if(lookahed.getToken() == TipoToken.SIMBOLO && lookahed.getLexema().equals("["))
		{
			consumirLexema(TipoToken.SIMBOLO, "[");
			consumir(TipoToken.VALNUM);
			consumirLexema(TipoToken.SIMBOLO, "]");
		}
	}
	
	
	private void Atribuicao() throws Exception {

		consumir(TipoToken.IDVAR);
		consumir(TipoToken.CMDATR);
		consumirValor();
		consumir(TipoToken.IDTERMINADOR);
	}
	
	private void ConsumirElse() throws Exception {
		
		consumirLexema(TipoToken.CMDIF, "else" );
		ConsumirComandos();
		consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
	}
	
	private void ConsumirComandos() throws Exception {

		while((lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("if")) ||
			  (lookahed.getToken() == TipoToken.CMDWHILE && lookahed.getLexema().equals("while")) ||
			  (lookahed.getToken() == TipoToken.IDVAR) ||
			  (lookahed.getToken() == TipoToken.IDCONTANTE) || 
			  (lookahed.getToken() == TipoToken.IDTIPO)
			 )
		{
			
			if(lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("if")) {
				EscopoCondicional();
				
			}else if(lookahed.getToken() == TipoToken.CMDWHILE && lookahed.getLexema().equals("while")) {
				EscopoRepeticao();
				
			}else if(lookahed.getToken() == TipoToken.IDVAR) {
				Atribuicao();
				
			}else if((lookahed.getToken() == TipoToken.IDCONTANTE) || (lookahed.getToken() == TipoToken.IDTIPO)) {
				EscopoDeclaracao();
				
			}else {
				ErroInesperadoSintatico();
				
			}	
		}
		
	}
	
	private void consumirLexema(TipoToken simbolo, String c) throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
		
		if(tokenAtual == simbolo && lookahed.getLexema().equals(c)) {
			lookahed = lexico.capturaToken();
			
		}
		else {
			ErroInesperadoSintatico();
			
		}
	}
	
	private void consumirExpBool() throws Exception {
		
		consumirItemExpBol();
		
		while (lookahed.getToken() != TipoToken.SIMBOLO && lookahed.getLexema() != ")") {
			
			consumirOpExpBol();
			consumirItemExpBol();
		}
	}
	
	private void ConsumirNegacao() throws Exception {
		consumirLexema(TipoToken.OPNEGACAO, "!");
		consumirLexema(TipoToken.SIMBOLO, "(" );
		consumirExpBool();
		consumirLexema(TipoToken.SIMBOLO, ")" );
	}
	
	private void consumirExpArit() throws Exception {
		consumirValorNum();
		
		if(lookahed.getLexema() != ";") {
			
			consumirOpArit();
			consumirValorNum();
			
			if (lookahed.getToken() == TipoToken.OPSUM ||
				lookahed.getToken() == TipoToken.OPSUB 	||
				lookahed.getToken() == TipoToken.OPMULT ||
				lookahed.getToken() == TipoToken.OPDIV ||
				lookahed.getToken() == TipoToken.OPMOD)
			{
				
				while (lookahed.getLexema() != ";")
				{
					consumirOpArit();
					consumirValorNum();
				}
			}
		}
	}
	
	private void consumirExpRel() throws Exception {

		consumir(TipoToken.IDVAR);
		consumirOpRel();
		consumir(TipoToken.IDVAR);
		consumir(TipoToken.IDTERMINADOR);
	}

	private void consumir(TipoToken token) throws Exception {
		TipoToken tokenAtual = lookahed.getToken();

		if(tokenAtual == token) {
			lookahed = lexico.capturaToken();
		
		}
		else {
			
			if(lookahed.getToken() == TipoToken.IDVAR) {
				Atribuicao();
				
			}else {
				ErroInesperadoSintatico();	
			}
		}
	}
	
	private void consumirItemExpBol() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
				
		switch (tokenAtual) {
		
		case VALBOOL:
			lookahed = lexico.capturaToken();
			break;	
			
		case IDVAR:
			lookahed = lexico.capturaToken();
			break;
			
		default:
			ErroInesperadoSintatico();
		}
	}
	
	private void consumirOpExpBol() throws Exception {
		
		if(lookahed.getToken() == TipoToken.OPAND || lookahed.getToken() == TipoToken.OPOR) {
			consumirOpLog();
		}
		else if((lookahed.getToken() == TipoToken.OPIGUAL) ||
				(lookahed.getToken() == TipoToken.OPMAIOR) ||
				(lookahed.getToken() == TipoToken.OPMENOR) ||
				(lookahed.getToken() == TipoToken.OPMAIORIGUAL) ||
				(lookahed.getToken() == TipoToken.OPMENORIGUAL) 
			   )
		{
			consumirOpRel();
		}
		else {
			ErroInesperadoSintatico();
		}
		
	}
	
	private void consumirValor() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
				
		switch (tokenAtual) {
		
		case VALFLOAT:
			if(lookahed.getToken() == TipoToken.VALFLOAT) {
				consumirExpArit();
				
			}else if(lookahed.getToken() == TipoToken.VALNUM){
				lookahed = lexico.capturaToken();
				
			}else {
				ErroInesperadoSintatico();
			}
			
			break;
			
		case VAL_STRING:
			lookahed = lexico.capturaToken();
			break;
		
		case VAL_CHAR:
			lookahed = lexico.capturaToken();
			break;	
		
		case VALBOOL:
			lookahed = lexico.capturaToken();
			break;	
			
		case VALNUM:
			if(lookahed.getToken() == TipoToken.VALNUM) {
				consumirExpArit();
				
			}else if(lookahed.getToken() == TipoToken.VALFLOAT){
				lookahed = lexico.capturaToken();
				
			}
			else {
				ErroInesperadoSintatico();
			}
			
			break;
			
		case IDVAR:
			if(lookahed.getToken() == TipoToken.IDVAR) {
				consumirExpArit();
			}else {
				lookahed = lexico.capturaToken();
			}
			
			break;
			
		default:
			ErroInesperadoSintatico();
		}
	}
	
	private void consumirValorUnico() throws Exception {

		TipoToken tokenAtual = lookahed.getToken();

		switch (tokenAtual) {
		case VALFLOAT:
			lookahed = lexico.capturaToken();
			break;

		case VAL_STRING:
			lookahed = lexico.capturaToken();
			break;

		case VAL_CHAR:
			lookahed = lexico.capturaToken();
			break;

		case VALBOOL:
			lookahed = lexico.capturaToken();
			break;

		case VALNUM:

			lookahed = lexico.capturaToken();
			break;

		case IDVAR:
			lookahed = lexico.capturaToken();

			break;

		default:
			ErroInesperadoSintatico();
		}
	}

	private void consumirValorNum() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
		switch (tokenAtual) {
		case VALFLOAT:
			lookahed = lexico.capturaToken();
			break;
			
		case VALNUM:
			lookahed = lexico.capturaToken();
			break;
		
		case IDVAR:
			lookahed = lexico.capturaToken();
			break;
			
		default:
			ErroInesperadoSintatico();
		}
	}

	private void consumirOpRel() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
		
		switch (tokenAtual) {
		case OPIGUAL:
			lookahed = lexico.capturaToken();
			break;
			
		case OPMAIOR:
			lookahed = lexico.capturaToken();
			break;
		
		case OPMENOR:
			lookahed = lexico.capturaToken();
			break;	
		
		case OPMAIORIGUAL:
			lookahed = lexico.capturaToken();
			break;	
			
		case OPMENORIGUAL:
			lookahed = lexico.capturaToken();
			break;
			
		default:
			ErroInesperadoSintatico();
		}
	}
	
	private void consumirOpLog() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
		
		switch (tokenAtual) {
		case OPAND:
			lookahed = lexico.capturaToken();
			break;
			
		case OPOR:
			lookahed = lexico.capturaToken();
			break;
		
		default:
			ErroInesperadoSintatico();
		}
	}

	private void consumirOpArit() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
		
		switch (tokenAtual) {
		case OPSUM:
			lookahed = lexico.capturaToken();
			break;
			
		case OPSUB:
			lookahed = lexico.capturaToken();
			break;
			
		case OPMULT:
			lookahed = lexico.capturaToken();
			break;
		
		case OPDIV:
			lookahed = lexico.capturaToken();
			break;
			
		case OPMOD:
			lookahed = lexico.capturaToken();
			break;
		default:
			ErroInesperadoSintatico();
		}
	}

	private void ErroInesperadoSintatico() throws Exception {
		System.err.println("Erro na linha " + lookahed.getLinha());
		System.err.println("Entrada Inesperada " + lookahed.getToken());
		throw new Exception("ERRO");
	}
	
	public static void main(String[] args) throws Exception {
		try {
			Sintatico sintatico = new Sintatico("teste.txt");
			sintatico.analise();
		} catch (Exception e) {
			
		}
		
	}
}
