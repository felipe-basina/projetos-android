package no.domain.googlesearchbooks.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by Felipe on 13/06/2016.
 */
public class HTTPUtils {

    public static String acessar(String endereco) {
        try {
            URL url = new URL(endereco);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is);
            String conteudo = scanner.useDelimiter("\\A").next();
            scanner.close();
            return conteudo;
        } catch (Exception e) {
            return null;
        }
    }
}
