package lexico;

public class Simbolos {
	public static boolean verificaSimbolo(char c) {
		if( c=='+' || c == '-' || c == '<'
				|| c=='/' || c==' '|| c == '>'
				|| c == '\n'|| c == '\t'  || c == '*' || c == '%' || c == ';' || c == 0)
			return true;
		return false;
	}
}
