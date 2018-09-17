/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uclust_il;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class UCLUST_IL {

    /**
     * @param args the command line arguments
     */
    public static LinkedList listL = new LinkedList();
    public static  String[] ordertokens;
    public static int listcount=0;
    public static LinkedList[] lists = new LinkedList[1000];;
    public static int tempcount=0;
    public static int[] findpos_1(int[] position,String[] tokens,String line,int i,ArrayList[] listA,int listAcount) {
        String attribute;
        int j;
        int[] j_pos= new int[10];
        for(int z=0;z<tokens.length;z++)
        {
            position[z] = line.indexOf(tokens[z], i+3);                                   
            attribute="";
            for(j=position[z];j<position[z]+tokens[z].length();j++) 
                attribute=attribute+line.charAt(j); 

            j_pos[z]=j;
        }
        extract_values(line,j_pos,tokens,listA,listAcount);
        return position;
    }

    public static void extract_values(String line,int[] j_pos,String[] tokens,ArrayList[] listA,int listAcount)
    {
        
        String value="";
        ArrayList temp;
        int valuecount[]=new int[]{1,1,1,1,1,1,1,1,1,1};
        int k=0;
        for(int i=0;i<ordertokens.length;i++)
        {
            value="";
            for(int j=0;j<tokens.length;j++)
            {
                if(ordertokens[i].equals(tokens[j]))
                {
                    k=j;
                    break;
                }
            }
            //case 1: eg: "firstname":"sekar"
            if(line.charAt(j_pos[k])=='"'&&line.charAt(j_pos[k]+1)==':'&&line.charAt(j_pos[k]+2)=='"'&&line.charAt(j_pos[k]+3)!='[')//&&((line.charAt(j_pos[k]+3) >= 'a' && line.charAt(j_pos[k]+3) <='z')||(line.charAt(j_pos[k]+3) >= 'A' && line.charAt(j_pos[k]+3) <='Z')))
            {
                j_pos[k]=j_pos[k]+3;
                for(;;j_pos[k]++)
                {
                    if((line.charAt(j_pos[k])=='"'&&line.charAt(j_pos[k]+1)==',') || (line.charAt(j_pos[k])=='"'&&line.charAt(j_pos[k]+1)=='}'))
                        break;
                    else
                        value=value+line.charAt(j_pos[k]);
                }   

                for(int l=0;l<=listAcount;l++)
                {
                    
                    listA[l].add(value);
                    valuecount[l]++;
                }
            }
            //check for case 2: eg: "languages":"[\"Tamil\",\"English\",\"Hindi\"]"
            else if(line.charAt(j_pos[k])=='"'&&line.charAt(j_pos[k]+1)==':'&&line.charAt(j_pos[k]+2)=='"'&&line.charAt(j_pos[k]+3)=='[')
            {
                for(j_pos[k]=j_pos[k]+4;;j_pos[k]++)
                {
                    System.out.println("character:"+line.charAt(j_pos[k]));
                    if((line.charAt(j_pos[k])=='"') && (line.charAt(j_pos[k]+1)==']') && (line.charAt(j_pos[k]+2)=='"'))
                        break;
                    else
                        value=value+line.charAt(j_pos[k]);
                }
                System.out.println(value);
                value=value.replace("\"", "");
                value=value.replace("\\", "");
                int n=0,count=0;
                String subvalue="";
                for(int m=0;m<value.length();m++)
                {
                    if((value.charAt(m)==',')||(m==value.length()-1))
                    {
                        count++;
                        if(m==value.length()-1)
                           subvalue=subvalue+value.charAt(m);  //add the last character
                        if(count!=1)
                        {
                            listAcount++;
                            valuecount[listAcount]=0;
                            for(int p=0;p<listA[listAcount-1].size()-1;p++)
                            {
                                if(p==0)
                                    listA[listAcount]=new ArrayList();
                                listA[listAcount].add(listA[listAcount-1].get(p));
                                valuecount[listAcount]++;
                            }
                        }
                        listA[listAcount].add(subvalue);
                        valuecount[listAcount]++;
                        subvalue="";
                        n++;
                    }
                    else
                        subvalue=subvalue+value.charAt(m);
                }
            }
        }
        for(int i=0;i<=listAcount;i++)
        {
            temp=new ArrayList();
            for(int j=0;j<valuecount[i];j++)
            {        
                temp.add(listA[i].get(j));
            }
            listL.add(temp);
        }
    }
    public static void cluster_listL(ListIterator iterator,int token)
    {
        String arrvalues[]=new String[1000];
        int count=0,arrayflag=0;
        int loopcount=0; 
        while(iterator.hasNext())
        {
            ArrayList element = (ArrayList) iterator.next();
            String tempvalue=element.get(token+1).toString();
            arrayflag=0;
            if(count==0)   //first distinct value
            {
                arrvalues[count]=tempvalue;
                count++;
            }
            else
            {
                for(int i=0;i<count;i++)
                {
                    if(tempvalue.equals(arrvalues[i]))   //check the current value with the existing arrvalues
                    {
                        if(i!=0)
                            tempcount=i-1;
                        arrayflag=1;
                        break;
                    }
                }
                if(arrayflag==0)   //a new distinct value is found
                {
                    arrvalues[count]=tempvalue;
                    tempcount=count-1;
                    count++;
                    if (lists[tempcount] == null) 
                    {
                        loopcount=1;
                        lists[tempcount] = new LinkedList();
                        listcount++;
                    }
                }
                ArrayList temp = new ArrayList();
                Iterator arriterator = element.iterator();
                while(arriterator.hasNext())
                {
                    String elementvalue = (String)arriterator.next();
                    temp.add(elementvalue);
                }
                if(!(tempvalue.equals(arrvalues[0])))
                {
                    lists[tempcount].add(temp);
                    iterator.remove();
                }
                Iterator listiterator=null;
                if(loopcount!=0)
                {
                    listiterator=lists[tempcount].iterator();
                    while(listiterator.hasNext())
                    {
                        ArrayList temp1=new ArrayList();
                        temp1=(ArrayList)listiterator.next();
                    }
                }
            }
        }
    }
    public static void cluster_lists(ListIterator iterator,int token,int prevtempcount)
    {
        String arrvalues[]=new String[1000];
        int count=0,arrayflag=0;
        int loopcount=0; 
        while(iterator.hasNext())
        {
            ArrayList element = (ArrayList) iterator.next();
            String tempvalue=element.get(token+1).toString();
            arrayflag=0;
            if(count==0)   //first distinct value
            {
                arrvalues[count]=tempvalue;
                count++;
            }
            else
            {
                for(int i=0;i<count;i++)
                {
                    if(tempvalue.equals(arrvalues[i]))   //check the current value with the existing arrvalues
                    {
                        if(i!=0)
                        {
                            tempcount=prevtempcount+i;
                        }
                        arrayflag=1;
                        break;
                    }
                }
                if(arrayflag==0)   //a new distinct value is found
                {
                    arrvalues[count]=tempvalue;
                    tempcount=listcount;
                    count++;
                    if (lists[tempcount] == null) 
                    {
                        loopcount=1;
                        lists[tempcount] = new LinkedList();
                        listcount++;
                    }
                }
                ArrayList temp = new ArrayList();
                Iterator arriterator = element.iterator();
                while(arriterator.hasNext())
                {
                    String elementvalue = (String)arriterator.next();
                    temp.add(elementvalue);
                }
                if(!(tempvalue.equals(arrvalues[0])))
                {
                    lists[tempcount].add(temp);
                    iterator.remove();
                }
                Iterator listiterator=null;
                if(loopcount!=0)
                {
                    listiterator=lists[tempcount].iterator();
                    while(listiterator.hasNext())
                    {
                        ArrayList temp1=new ArrayList();
                        temp1=(ArrayList)listiterator.next();
                    }
                }
            }
        }
    }
    public static void traverse_list(int tokenlength)
    {
        ListIterator iterator;
        int token=0;
        int prevtempcount=0;
        int lastcount=0;
        while(token<tokenlength)
        {
            if(token==0)
            {
                iterator=listL.listIterator();
                cluster_listL(iterator,token);
            }
            else
            {
                prevtempcount=listcount-1;
                lastcount=prevtempcount;
                iterator=listL.listIterator();
                cluster_lists(iterator,token,prevtempcount);
                prevtempcount=listcount-1;
                for(int i=0;i<=lastcount;i++)
                {
                    iterator=lists[i].listIterator();
                    cluster_lists(iterator,token,prevtempcount);  
                    prevtempcount=listcount-1;
                }
            } 
            token++;
        }
    }
    public static void main(String[] args) {
        // TODO code application logic here
        BufferedReader br = null;
        FileInputStream inputStream = null;
        Scanner sc = null;
        String line,key="",inputattr="",value="";
        try
        {
            System.out.println("Enter the attribute to be clustered:");
            br=new BufferedReader(new InputStreamReader(System.in));
            inputattr=br.readLine();
            int linecount=0;
            int tokenlength;
            int[] position=new int[10];
            inputStream = new FileInputStream("/home/pglab1/Desktop/sathya/uCLUST_IL/src/uclust_il/image_metadata1.json");
            sc = new Scanner(inputStream, "UTF-8");
            String delims = "[,]";
      
            String[] tokens = inputattr.split(delims);
            tokenlength=tokens.length;
            ordertokens=new String[tokens.length];
            System.arraycopy(tokens, 0, ordertokens, 0, tokens.length);
            while (sc.hasNextLine()) 
            {
                line = sc.nextLine();
                key="";
                value="";
                ArrayList<String> listA[] = new ArrayList[10];
                int listAcount=0;
                if((line.charAt(0)=='[') && (line.charAt(1)=='{'))
                {
                    continue;
                }   
                else
                {
                    linecount++;
                    for(int i=1;;i++)
                    {
                        if(line.charAt(i)=='"'&&line.charAt(i+1)==':'&&line.charAt(i+2)=='{')    //key got separated and now process the attributes
                        {
                            listA[listAcount]=new ArrayList();
                            listA[listAcount].add(key);
                            position=findpos_1(position,tokens,line,i,listA,listAcount);
                            break;
                        }
                        key=key+line.charAt(i);
                    }
                }
            }
            System.out.println(listL);
            traverse_list(tokenlength);
            ListIterator iterator = listL.listIterator();
            System.out.println("listL values:");
            iterator = listL.listIterator();
            while(iterator.hasNext())
            {               
                ArrayList element = (ArrayList) iterator.next();
                System.out.println(element); 
            }
            for(int i=0;i<listcount;i++)
            {
                System.out.println("list :"+i);
                iterator=lists[i].listIterator();
                while(iterator.hasNext())
                {
                    ArrayList element = (ArrayList) iterator.next();
                    System.out.println(element); 
                }                
            }
        }
        catch(Exception e){}
    }
}


