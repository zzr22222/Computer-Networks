package UDP;

public class Message {
    public enum Type {
        REGISTER, CHAT, UNREGISTER, FILE
    }

    private Type type;
    private String sender;
    private String receiver;
    private String content;
//    private int chunk;
//    private int totalChunks;

    public Message(Type type, String sender, String receiver, String content) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
      //  this.chunk = chunk;
       // this.totalChunks = totalChunks;
    }

    public Type getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

//    public int getChunk() {
//        return chunk;
//    }
//
//    public int getTotalChunks() {
//        return totalChunks;
//    }

    public static Message fromString(String text) {
        String[] parts = text.split("\\|", 6);
        Type type = Type.valueOf(parts[0]);
        String sender = parts[1].isEmpty() ? null : parts[1];
        String receiver = parts[2].isEmpty() ? null : parts[2];
        String content = parts[3].isEmpty() ? null : parts[3];
      //  int chunk = Integer.parseInt(parts[4]);
     //   int totalChunks = Integer.parseInt(parts[5]);
        return new Message(type, sender, receiver, content);
    }

    @Override
    public String toString() {
        String typeText = type.toString();
        String senderText = sender == null ? "" : sender;
        String receiverText = receiver == null ? "" : receiver;
        String contentText = content == null ? "" : content;
      //  String chunkText = Integer.toString(chunk);
      //  String totalChunksText = Integer.toString(totalChunks);
        return String.join("|", typeText, senderText, receiverText, contentText
        );
    }
}