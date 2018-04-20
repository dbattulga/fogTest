import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
//Flink Consumer

public class Main{
	public static void main(String argv[]) throws Exception{
        
	    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	    
	    Properties properties = new Properties();
	    properties.setProperty("bootstrap.servers", "localhost:9092");
	    properties.setProperty("group.id", "test");
	    
	    final int max = 10;
	    //final Map<String, Integer> maxMap = new HashMap<>(max);
	    final List<entries> maxList = new ArrayList<entries>();
	    
        DataStream<Tuple2<String, Integer>> dataStream = env
        		.addSource(new FlinkKafkaConsumer011<>("testone", new SimpleStringSchema(), properties) )
                .flatMap(new LineSplitter())
                .keyBy(0)
                .sum(1)
                .keyBy(0)
                .timeWindowAll(Time.seconds(2))
                .apply(new AllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
                	
                    @Override
                    public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                    	
                		if (maxList.isEmpty()) {
                			List<entries> unsList = new ArrayList<entries>();
                        	for (Tuple2<String, Integer> value: values) {        
                        		String key = value.f0;
                        		int count = value.f1;
                        		unsList.add(new entries(key, count));
                        	}
                        	Collections.sort(unsList, Collections.reverseOrder());
                    		
                        	int x = 0;
                        	for(int i = 0; i < unsList.size(); i++) {
                        		
                        		if (maxList.isEmpty()) {
                        			maxList.add(new entries(unsList.get(0).key, unsList.get(0).count));
                        			i++;
                        			if (unsList.size()==i) break;
                        		}
                        		
                        		if (!maxList.contains(unsList.get(i))) {
                        			maxList.add(new entries(unsList.get(i).key, unsList.get(i).count));
                        			x++;
                        		}
                        		
                        		if (x == max-1)
                        			break;
                        	}
                        	for (int i=0; i<maxList.size(); i++) {
                    			out.collect(new Tuple2(maxList.get(i).key, maxList.get(i).count));
                    		}
                		}

                		else {
                			List<entries> unsList = new ArrayList<entries>();
                        	for (Tuple2<String, Integer> value: values) {        
                        		String key = value.f0;
                        		int count = value.f1;
                        		unsList.add(new entries(key, count));
                        	}
                        	for (int i=0; i<maxList.size(); i++) {
                        		unsList.add(new entries(maxList.get(i).key, maxList.get(i).count));
                        	}
                    		
                        	Collections.sort(unsList, Collections.reverseOrder());
                        	maxList.clear();
                        	
                        	int x = 0;
                        	for(int i = 0; i < unsList.size(); i++) {
                        		
                        		if (maxList.isEmpty()) {
                        			maxList.add(new entries(unsList.get(0).key, unsList.get(0).count));
                        			i++;
                        			if (unsList.size()==i) break;
                        		}
                        		if (!maxList.contains(unsList.get(i))) {
                        			maxList.add(new entries(unsList.get(i).key, unsList.get(i).count));
                        			x++;
                        		}                      		
                        		if (x == max-1)
                        			break;
                        	}
                        	for (int i=0; i<maxList.size(); i++) {
                    			out.collect(new Tuple2(maxList.get(i).key, maxList.get(i).count));
                    		}
                		}
                		writeFile(maxList);
                		System.out.println("---------------");
                		
                    }
                });

        
        	//dataStream.writeAsText("text.txt", WriteMode.OVERWRITE);
        	dataStream.print();
	        env.execute("Streaming");
	    }
	
		public static void writeFile(List<entries> list) throws IOException {
			File fout = new File("/home/text.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			for (int i = 0; i < list.size(); i++) {
				String build = "("+list.get(i).key+","+list.get(i).count+")";
				bw.write(build);
				bw.newLine();
			}
		 
			bw.close();
		}

		public static class entries implements Comparable<entries> {
			String key;
		    Integer count;
	
		    public entries(String key, Integer count) {
		        this.key = key;
		        this.count = count;
		    }
		    public String getKey() { return this.key;}
		    public Integer getCount() { return this.count;}

			@Override
			public int compareTo(entries arg0) {
				return this.getCount().compareTo(arg0.getCount());
			}
			@Override
			public boolean equals (Object object) {
			    boolean result = false;
			    if (object == null || object.getClass() != getClass()) {
			        result = false;
			    } else {
			        entries e = (entries) object;
			        if (this.key.equals(e.key)) {
			            result = true;
			        }
			    }
			    return result;
			}
		    
		}
		
		public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
	
	        @Override
	        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
	            String[] tokens = value.toLowerCase().split("\\W+");
	
	            for (String token : tokens) {
	                if (token.length() > 0) {
	                    out.collect(new Tuple2<String, Integer>(token, 1));
	                }
	            }
	        }
	    }
}