package net.zylklab.sandbox.udp.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import net.zylklab.sandbox.udp.avro.MeasureRecord;

public class UdpClient {
	
	private static List<MeasureRecord> buildMeasureList(int i) {
		List<MeasureRecord> measures = new ArrayList<MeasureRecord>();
		measures.add(new MeasureRecord(String.format("var-%s", i), i,System.currentTimeMillis()));
		return measures;
	}
	
	private static byte[] records2Bytes(List<MeasureRecord> list) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
		DatumWriter<MeasureRecord> writer = new SpecificDatumWriter<MeasureRecord>(MeasureRecord.getClassSchema());
		for (MeasureRecord measureRecord : list) {
			writer.write(measureRecord, encoder);	
		}
		encoder.flush();
		out.close();
		byte[] serializedBytes = out.toByteArray();
		return serializedBytes;
	}
	
	
    public static void main(String args[]) throws IOException, InterruptedException{
    	int events = 100;
    	InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        DatagramPacket packet;
        byte[] buf;
        long t0 = System.currentTimeMillis();
        try {
        	for(int i = 0; i < events; i++) {
        		buf =  UdpClient.records2Bytes(UdpClient.buildMeasureList(i));
        		packet = new DatagramPacket(buf, buf.length, address, 9956);
        		//https://stackoverflow.com/questions/4956206/change-linux-kernel-timer
        		//Thread.sleep(0,100000);
        		socket.send(packet);
        		System.out.println("int: "+i+ " size: "+buf.length);
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo de envío (ms): "+(t1-t0));
        System.out.println("Tiempo medio de envío (ms): "+(t1-t0)/new Double(events));
        System.out.println("Eventos por segundo: "+events*1000/new Double((t1-t0)));
    }
}
