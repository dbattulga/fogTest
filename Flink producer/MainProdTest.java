

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
//Flink producer

public class MainProdTest{
	 public static void main(String[] args) throws Exception {
		    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		    Properties properties = new Properties();
		    properties.setProperty("bootstrap.servers", "localhost:9092"); 

		    DataStream<String> stream = env.addSource(new SimpleStringGenerator());
		    stream.addSink(new FlinkKafkaProducer011<>("testone", new SimpleStringSchema(), properties));

		    env.execute();
		  }
	 
	  public static class SimpleStringGenerator implements SourceFunction<String> {
		  	
		    boolean running = true;
		    long i = 0;
		    @Override
		    public void run(SourceContext<String> ctx) throws Exception {
		        
		    	ArrayList<String> list = new ArrayList<String>();
		    	InputStream is = getClass().getResourceAsStream("/words.txt");
			    InputStreamReader isr = new InputStreamReader(is);
			    BufferedReader br = new BufferedReader(isr);
			    String line;
			    while ((line = br.readLine()) != null) 
			    {
			    	list.add(line);
			    }
			    br.close();
			    isr.close();
			    is.close();
		    	
		      while(running) {
		    	String random = list.get(new Random().nextInt(list.size()));
		        ctx.collect(random);
		        Thread.sleep(200);
		      }
		    }
		    @Override
		    public void cancel() {
		      running = false;
		    }
	  }

}