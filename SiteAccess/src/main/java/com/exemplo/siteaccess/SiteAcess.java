package com.exemplo.siteaccess;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SiteAcess {
    public static void main(String[] args) {
        String siteURL = "https://rafaeleliasioppi.github.io/rafael/";

        while (true) {
            try (PrintWriter log = new PrintWriter(new FileWriter("log_acessos.txt", true))) {
                
                Document doc = Jsoup.connect(siteURL).get();

                // Acessa os links do menu
                Elements mainLinks = doc.select(".menu a[href]");
                for (int i = 0; i < Math.min(5, mainLinks.size()); i++) {
                    String url = mainLinks.get(i).attr("abs:href");
                    if (url.contains("rafaeleliasioppi.github.io") && !url.endsWith("#")) {
                        acessar(url, log);
                    }
                }

                // Acessa os botões de produto
                Elements botoes = doc.select("a.botao-link");
                for (int i = 0; i < Math.min(5, botoes.size()); i++) {
                    String url = botoes.get(i).attr("abs:href");
                    acessar(url, log);

                    // Acessa links internos da subpágina
                    try {
                        Document subPage = Jsoup.connect(url).get();
                        Elements internos = subPage.select("a[href]");
                        for (int j = 0; j < Math.min(3, internos.size()); j++) {
                            String internoUrl = internos.get(j).attr("abs:href");
                            if (internoUrl.contains("rafaeleliasioppi.github.io")) {
                                acessar(internoUrl, log);
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("❌ Falha na subpágina: " + ex.getMessage());
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ Erro geral: " + e.getMessage());
            }

            try {
                Thread.sleep(10000); // espera 10 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Método auxiliar
    private static void acessar(String urlStr, PrintWriter log) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int status = conn.getResponseCode();
            conn.disconnect();

            System.out.println("🌐 Visitando → " + urlStr + " [Status: " + status + "]");
            log.println("[" + java.time.LocalDateTime.now() + "] Acessado: " + urlStr + " [Status: " + status + "]");
        } catch (Exception e) {
            System.out.println("⚠️ Erro em: " + urlStr + " → " + e.getMessage());
            log.println("[" + java.time.LocalDateTime.now() + "] Erro: " + urlStr + " → " + e.getMessage());
        }
    }
}
