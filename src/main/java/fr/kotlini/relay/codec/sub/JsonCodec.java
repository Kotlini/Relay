package fr.kotlini.relay.codec.sub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.kotlini.relay.codec.CodecType;
import fr.kotlini.relay.codec.ICodec;
import fr.kotlini.relay.message.MessageType;

import java.nio.charset.StandardCharsets;

public class JsonCodec implements ICodec {

    private final Gson gson;

    public JsonCodec() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    public JsonCodec(Gson customGson) {
        this.gson = customGson;
    }

    @Override
    public byte[] encode(Object object) throws Exception {
        String json = gson.toJson(object);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz) throws Exception {
        String json = new String(data, StandardCharsets.UTF_8);
        return gson.fromJson(json, clazz);
    }

    @Override
    public MessageType decodeType(byte[] data) throws Exception {
        String json = new String(data, StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("originalMessageId")) {
            return MessageType.RESPONSE;
        }
        return MessageType.REQUEST;
    }

    @Override
    public CodecType getCodecType() {
        return CodecType.JSON;
    }
}