package project.wpl.constants;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Constants {
    
     
     public static Set<String> set = Stream.of("What primary school did you attend?", 
    		 "What was the house number and street name you lived in as a child?", 
    		 "What were the last four digits of your childhood telephone number?")
    		  .collect(Collectors.toCollection(HashSet::new));
     
    
     
     
}
			