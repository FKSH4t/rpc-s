package com.whedc.serial;

import java.io.*;

/**
 * 使用Java原生的序列化和反序列化方法
 */
public class JdkSerializer implements Serializer{

    /**
     * 使用Java对象流进行序列化
     * @param object 待序列化对象
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        byte[] byteArray = outputStream.toByteArray();
        outputStream.close();
        return byteArray;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try (inputStream; ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
