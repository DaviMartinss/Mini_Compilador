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
		//Atribuicao();
		Constante();
		//P();
		//E();
		//EscopoRepeticao();
		//EscopoCondicional();
		//ConsumirComandos();
		//consumirExpArit();
		//consumirExpRel();
		EscopoDeclaracao();
		consumir(TipoToken.EOF);
		System.out.println("Análise Finalizada");
	}

	private void P() throws Exception {
		 
		consumir(TipoToken.IDTIPO);
		consumir(TipoToken.IDVAR);
		consumir(TipoToken.IDTERMINADOR);
	}
	

	private void Constante() throws Exception {
		 
		consumir(TipoToken.IDCONTANTE);
		consumir(TipoToken.IDTIPO);
		consumir(TipoToken.IDVAR);
		consumir(TipoToken.CMDATR);
		consumirValorUnico();
	}
	
	private void EscopoDeclaracao() throws Exception {
		 
		consumir(TipoToken.IDTIPO);
		consumir(TipoToken.IDVAR);
		
		while (lookahed.getToken() != TipoToken.IDTERMINADOR) {
			
			consumirLexema(TipoToken.SIMBOLO, ",");
			consumir(TipoToken.IDVAR);
		}
		
		consumir(TipoToken.IDTERMINADOR);
	}
	
	private void Atribuicao() throws Exception {

		consumir(TipoToken.IDVAR);
		consumir(TipoToken.CMDATR);
		consumirValor();
		consumir(TipoToken.IDTERMINADOR);
	}
	
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
		}
	}
	
	private void ConsumirIfParcial() throws Exception {
		consumir(TipoToken.CMDIF);
		consumirLexema(TipoToken.SIMBOLO, "(" );
		
		if(lookahed.getToken() == TipoToken.OPNEGACAO)
			ConsumirNegacao();
		else
			consumirExpBool();
		
		consumirLexema(TipoToken.SIMBOLO, ")" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "then" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
	}
	
	private void ConsumirIfCompleto() throws Exception {
		consumir(TipoToken.CMDIF);
		consumirLexema(TipoToken.SIMBOLO, "(" );
		
		if(lookahed.getToken() == TipoToken.OPNEGACAO)
			ConsumirNegacao();
		else
			consumirExpBool();
		
		consumirLexema(TipoToken.SIMBOLO, ")" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "then" );
		ConsumirIfCompleto();
		consumirLexema(TipoToken.CMDIF, "else" );
		consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
	}
	
	private void ConsumirElse() throws Exception {
		consumirLexema(TipoToken.CMDIF, "else" );
		ConsumirComandos();
		consumirLexema(TipoToken.PALAVRA_CHAVE, "end" );
	}
	
	private void ConsumirComandos() throws Exception {

		while((lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("if")) ||
			  (lookahed.getToken() == TipoToken.CMDWHILE && lookahed.getLexema().equals("while")) ||
			  (lookahed.getToken() == TipoToken.IDVAR)	
			 )
		{
			
			if(lookahed.getToken() == TipoToken.CMDIF && lookahed.getLexema().equals("if")) {
				EscopoCondicional();
				
			}else if(lookahed.getToken() == TipoToken.CMDWHILE && lookahed.getLexema().equals("while")) {
				EscopoRepeticao();
				
			}else if(lookahed.getToken() == TipoToken.IDVAR) {
				Atribuicao();
			}else {
				System.err.println("Erro na linha "+lookahed.getLinha());
				System.err.println("entrada inválida "+lookahed.getToken());
				throw new Exception("ERRO");
			}	
		}
		
	}
	
	
	//Escopo geral do while
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

	private void consumirLexema(TipoToken simbolo, String c) throws Exception {
		TipoToken tokenAtual = lookahed.getToken();
		if(tokenAtual == simbolo && lookahed.getLexema().equals(c)) {
			lookahed = lexico.capturaToken();
		}
		else {
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
	
	private void E() throws Exception {
		//if(lookahed.getToken() == TipoToken.IDVAR || lookahed.getToken() == TipoToken.OPIGUAL) {
			consumir(TipoToken.IDVAR);
			if(lookahed.getToken() == TipoToken.OPIGUAL)
					Operadores();
			if(lookahed.getToken() == TipoToken.EOF);
			else	erro();
		}
		

	private void Operadores() throws Exception {
		consumir(TipoToken.OPIGUAL);
		if(lookahed.getToken() == TipoToken.IDVAR)
			E();
		else
			erro();
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
				System.err.println("Erro na linha "+lookahed.getLinha());
				System.err.println("entrada inválida "+lookahed.getToken());
				throw new Exception("ERRO");	
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
		}
		
	}
	
	private void consumirValor() throws Exception {
		
		TipoToken tokenAtual = lookahed.getToken();
				
		switch (tokenAtual) {
		case VALFLOAT:
			if(lookahed.getToken() == TipoToken.VALFLOAT) {
				consumirExpArit();
			}else {
				lookahed = lexico.capturaToken();
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
			}else {
				lookahed = lexico.capturaToken();
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha " + lookahed.getLinha());
			System.err.println("entrada inválida " + lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
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
			System.err.println("Erro na linha "+lookahed.getLinha());
			System.err.println("entrada inválida "+lookahed.getToken());
			throw new Exception("ERRO");
		}
	}

	private void erro() {
		System.err.println("Erro sintatico linha: "+lookahed.getLinha());
	}
	
	public static void main(String[] args) throws Exception {
		try {
			Sintatico sintatico = new Sintatico("teste.txt");
			sintatico.analise();
		} catch (Exception e) {
			
		}
		
	}
}
