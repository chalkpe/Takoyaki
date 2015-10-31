package pe.chalk.takoyaki.bot;

import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.model.message.MessageType;
import de.vivistra.telegrambot.model.message.TextMessage;
import de.vivistra.telegrambot.receiver.IReceiverService;
import de.vivistra.telegrambot.receiver.Receiver;
import de.vivistra.telegrambot.sender.Sender;
import de.vivistra.telegrambot.settings.BotSettings;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.utils.TextFormat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-10-02
 */
public class TakoyakiBot implements IReceiverService {
    private Set<Integer> recipients = new HashSet<>();

    public TakoyakiBot(String token) throws IOException {
        BotSettings.setApiToken(token);
        Receiver.subscribe(this);

        Takoyaki takoyaki = new Takoyaki();
        takoyaki.getLogger().addTransmitter((level, message) -> recipients.forEach(id -> Sender.send(new TextMessage(id, TextFormat.decode(message, TextFormat.Type.NONE)))));
        takoyaki.start();
    }

    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            throw new IllegalArgumentException();
        }

        new TakoyakiBot(args[0]);
    }

    public void received(Message message){
        if(message.getMessageType() != MessageType.TEXT_MESSAGE){
            return;
        }

        String command = ((TextMessage) message).getMessage();
        if(!command.startsWith("/")){
            return;
        }

        String[] commands = command.substring(1).split(" ");
        if(!commands[0].equalsIgnoreCase("takoyaki") && !commands[0].equalsIgnoreCase("takoyaki@takoyakibot")){
            return;
        }

        if(commands.length < 2){
            return;
        }

        int target = message.isFromGroupChat() ? message.getGroupChat().getId() : message.getSender().getId();
        switch(commands[1].toLowerCase()){
            case "start":
                this.recipients.add(target);
                break;

            case "stop":
                this.recipients.remove(target);
                break;

            default:
                return;
        }

        System.out.println(TextFormat.RED.getAnsiCode() + this.recipients.toString() + TextFormat.RESET.getAnsiCode());
    }
}
