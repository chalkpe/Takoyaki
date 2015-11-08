package pe.chalk.takoyaki.bot;

import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.model.message.MessageType;
import de.vivistra.telegrambot.model.message.TextMessage;
import de.vivistra.telegrambot.receiver.IReceiverService;
import de.vivistra.telegrambot.receiver.Receiver;
import de.vivistra.telegrambot.sender.Sender;
import de.vivistra.telegrambot.settings.BotSettings;
import org.json.JSONObject;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.logger.Loggable;
import pe.chalk.takoyaki.logger.LoggerTransmitter;
import pe.chalk.takoyaki.plugin.PluginBase;
import pe.chalk.takoyaki.utils.TextFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-10-02
 */
public class TakoyakiBot extends PluginBase implements IReceiverService {
    private final Set<Integer> recipients = new HashSet<>();
    private final LoggerTransmitter transmitter = (level, message) -> this.recipients.forEach(id -> Sender.send(new TextMessage(id, TextFormat.decode(message, TextFormat.Type.NONE))));

    public TakoyakiBot(){
        super("TakoyakiBot");

        try{
            Path propertiesPath = Paths.get("TakoyakiBot.json");
            if(Files.notExists(propertiesPath)) Files.write(propertiesPath, Arrays.asList("{", "    \"token\": \"YOUR_BOT_TOKEN\"", "}"), StandardCharsets.UTF_8);

            this.init(new JSONObject(new String(Files.readAllBytes(propertiesPath), StandardCharsets.UTF_8)));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void init(final JSONObject properties) throws IOException {
        String token = properties.getString("token");
        if(token.equals("YOUR_BOT_TOKEN")) throw new IllegalArgumentException("You must set your bot token at ./TakoyakiBot.json");

        BotSettings.setApiToken(token);

        Receiver.subscribe(this);
        Takoyaki.getInstance().getLogger().addTransmitter(this.transmitter);
    }

    public void received(final Message message){
        if(message.getMessageType() != MessageType.TEXT_MESSAGE) return;

        final String command = ((TextMessage) message).getMessage();
        if(!command.startsWith("/")) return;

        final String[] commands = command.substring(1).split(" ");
        if(!(commands[0].equalsIgnoreCase("takoyaki") || commands[0].equalsIgnoreCase("takoyaki@takoyakibot")) || commands.length < 2) return;

        final int target = message.isFromGroupChat() ? message.getGroupChat().getId() : message.getSender().getId();
        switch(commands[1].toLowerCase()){
            case "start":
                this.recipients.add(target);
                break;

            case "stop":
            case "shutdown":
                this.recipients.remove(target);
                break;

            default:
                return;
        }

        System.out.println(Loggable.Level.DEBUG.getFormats() + this.recipients.toString() + TextFormat.RESET.getAnsiCode());
    }

    @Override
    public void onDestroy(){
        Receiver.unsubscribe(this);
        Takoyaki.getInstance().getLogger().removeTransmitter(this.transmitter);
    }
}
