# uCLUST
Algorithm for Clustering Unstructured Data
Input : JSON file, clustering attributes
Output : Clusters formed with the clustering attributes
1. Get the clustering attributes as input from the user.   
#Formation of nodes for the records in JSON file    
2. Read each record from the input json file.    
3. Extract the recordkey of each record.     
4. Find the position of the clustering attribute from each line and retrieve its corresponding value/values.   
5. For the first clusterng attribute,     
     -> If the clustering attribute is single-valued, create a node with the recordkey as its first field and its value as the second field.      
        E.g: "Student1":{"name":"xxx","gender":"Female",lang":["Hindi","English"]} -> the node will be formed as     |Student1|Female| for the attribute 'gender'.        
     -> If the clustering attribute is multi-valued, create nodes equal to the number of values corresponding to the attribute. Each node will hold the recordkey as the first field and the value as the second field.    
        E.g: "Student1":{"name":"xxx","gender":"Female",lang":["Hindi","English"]} -> the nodes will be formed as |Student1|Hindi| and |Student1|English| for the attribute 'lang'.     
6. For other clustering attributes,
     add the values of the clustering attributes as the next field to the already created node with the respective recordkey.      
        E.g: "Student1":{"name":"xxx","gender":"Female",lang":["Hindi","English"]} -> the nodes will be formed as
        |Student1|Female|Hindi| and |Student1|Female|English| for the attributes 'gender' and 'lang'.    
#Formation of the linked list    
7. These extracted nodes are added together to form a singly-linked list as shown below:    
       |Student1|Female|Hindi| - |Student1|Female|English| - |Student2|Male|Tamil| - |Student2|Male|English|     
#Traversing the linked list    
8. Traverse each node in the linked list.    
9. For each clustering attribute i,   
      -> examine the (i+1)th field in the node   
      -> When the first distinct value is found, retain the node as such in the list.   
      -> When a new distinct value is found, create a new list for that distinct value.   
      -> When a field value matches with an already found value, then store the node in the corresponding list 
         with the same value.    
      -> After moving the node to the corresponding list, delete the node from the list.   
10. Output all the lists formed representing the clusters. Each list represents an unique cluster.   
