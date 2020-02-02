public class App
{
    public static void main(String[] args)
    {
    	runProducer();
    	//runConsumer();        
    }
    static void runProducer()
    {
    	Producer<Long, String> producer=producerCreator.createProducer();
    	
    }
}