package pe.chalk.takoyaki;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pe.chalk.takoyaki.logger.PrefixedLogger;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-09-27
 */
public class Staff extends WebClient {
    private PrefixedLogger logger;

    public Staff(PrefixedLogger logger, JSONObject accountProperties, int timeout){
        super(BrowserVersion.CHROME);
        this.getOptions().setTimeout(timeout);
        this.getOptions().setJavaScriptEnabled(false);
        this.getOptions().setCssEnabled(false);

        this.logger = logger;

        Logger.getLogger("org.apache").setLevel(Level.OFF);
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        this.logger.info("네이버에 로그인합니다: " + accountProperties.getString("username"));
        this.login(accountProperties);
    }

    private void login(JSONObject accountProperties){
        try{
            final HtmlPage loginPage = this.getPage("https://nid.naver.com/nidlogin.login?url=");
            final HtmlForm loginForm = loginPage.getFormByName("frmNIDLogin");

            final HtmlTextInput     idInput     = loginForm.getInputByName("id");
            final HtmlPasswordInput pwInput     = loginForm.getInputByName("pw");
            final HtmlSubmitInput   loginButton = (HtmlSubmitInput) loginForm.getByXPath("//fieldset/span/input").get(0);

            final String id = accountProperties.getString("username");
            final String pw = accountProperties.getString("password");

            if(id.equals("") || pw.equals("")){
                this.logger.notice("네이버 로그인 불가: 계정 정보가 설정되어 있지 않습니다");
                return;
            }

            idInput.setValueAttribute(id);
            pwInput.setValueAttribute(pw);

            Elements errors = Jsoup.parse(((HtmlPage) loginButton.click()).asXml().replaceFirst("<\\?xml version=\"1.0\" encoding=\"(.+)\"\\?>", "<!DOCTYPE html>")).select("div.error");
            if(!errors.isEmpty()){
                this.logger.warning("네이버 로그인 실패: " + errors.text());
            }
        }catch(Exception e){
            this.logger.warning("네이버 로그인 실패: " + e.getClass().getName() + ": " + e.getMessage());
        }

        this.close();
    }

    public Document parse(String url) throws IOException {
        return this.parse(new URL(url));
    }

    public Document parse(URL url) throws IOException {
        final String html = this.getPage(url).getWebResponse().getContentAsString();

        this.close();
        return Jsoup.parse(html);
    }
}
