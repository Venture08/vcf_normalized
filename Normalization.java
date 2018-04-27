/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcf_normalize;

import java.io.BufferedReader;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
//import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedList;
import java.util.List;
//import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author huwq
 */
public class Normalization {
    public static void main(String[] args)throws IOException{
        //定义文件路径
        //Path path = Paths.get("/home/huwq/Documents/company/VCF");        
        //String filename = "/test.txt";
        //System.out.println(path+filename);
        //建立文件空间
        File file = new File("/home/huwq/Documents/company/VCF/test.txt");
        //读取文件
        List<String> vcfGene = readData(file);
        //对vcf进行标准化；
        normalize(vcfGene);
        
    }
    
  
    
    public static List<String> readData(File f) throws IOException{
        List<String> r = new ArrayList<>();
        FileWriter writer = new FileWriter(
                "/home/huwq/Documents/company/VCF/normalized_vcf_example",false
        );
        String encoding ="UTF-8";
        if (f.isFile() && f.exists()){
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(f)
            );
            BufferedReader bufferR = new BufferedReader(reader);
            String line = "";
            while ((line = bufferR.readLine())!= null){
                if(line.startsWith("#")){ 
                    writer.write(line+"\n");                    
                }                
                else{
                    //增加正式文本行
                    r.add(line.trim());                                                                             
                }                
            }           
        }
        writer.close();
        return r; 
        
    }
       
    //@SuppressWarnings("empty-statement")
    public static void normalize(List<String> vcfGene) throws IOException{                
        FileWriter writer = new FileWriter(
                "/home/huwq/Documents/company/VCF/normalized_vcf_example",true);
        //System.out.println("Total line:"+vcfGene.size()); 
        
        //比较ref与alt是否符合左对齐以及最简原则
        for(int i = 0 ; i < vcfGene.size()-1; i++){                                              
            String[] vcfGeneBuffer = vcfGene.get(i).trim().split("\\s+",10); 
                        
            //ref代表参考序列，alt代表变异序列
            String ref = vcfGeneBuffer[3];
            String alt = vcfGeneBuffer[4];  
                                                        
            
            StringBuffer refBuffer = new StringBuffer();
            StringBuffer altBuffer = new StringBuffer();
            
            //System.out.println(ref+"--ref");
            //System.out.println(alt+"--alt");
            
            //如果长度为0或者完全一致的话，过滤掉
            if(ref.equals(".") || alt.equals(".") || ref.equals(alt)){ 
                //System.out.println("yes");
                continue;
            }
                                   
            //如果是SNP位点，则变异位点和参考位点长度应一致
            if(ref.length() ==  alt.length()){
                
                //去掉两端冗余字段;
                for(int j = 0; j < ref.length(); j++){
                    if(ref.charAt(j) == alt.charAt(j)){
                        //refBuffer.replace(j,j+1,"");                        
                        altBuffer.replace(j,j+1," ");                        
                        //ref.deleteCharAt(j);
                        //alt.deleteCharAt(j);                         
                    }
                    else{
                        refBuffer.append(ref.charAt(j));
                        altBuffer.append(alt.charAt(j));
                    }
                }
                //还原为字符串           
                String refString = refBuffer.toString();
                String altString = altBuffer.toString();
                //System.out.println(refString);
                               
                //替换存在冗余的字段
                String vcfGeneReplace = vcfGene.get(i).replace(ref, refString);
                vcfGeneReplace = vcfGeneReplace.replace(alt, altString);                
                //System.out.println("repalce -"+vcfGeneReplace);                                               
                writer.write(vcfGeneReplace+"\n");
            }
            //如果是INDEL位点，则变异位点和参考位点长度不一致，且会左对齐(最左边字符串一致)
            else if(ref.length()!=  alt.length()) {
                if(ref.length()>=2 && alt.length()>= 2){
                    //求出两个字符串最小的长度
                    int lenMin =Integer.min(ref.length()-1,alt.length()-1);
                    refBuffer.append(ref);
                    altBuffer.append(alt);                    
         
                    for(int j = 0; j < lenMin; j++){
                        //去掉左边第一个相同的字符串
                        if (ref.charAt(j) == alt.charAt(j) && ref.charAt(j+1) != alt.charAt(j+1)) {
                            
                            //ref.deleteCharAt(0);
                            //alt.deleteCharAt(0);
                        }
                        else{
                            refBuffer = refBuffer.replace(j,j+1,"");
                            altBuffer = altBuffer.replace(j,j+1," ");                            
                            //refBuffer.append(ref.charAt(j));
                            //altBuffer.append(alt.charAt(j));
                        }
                    }
                    
                     //最后进行还原操作                    
                    String refString = refBuffer.toString();
                    String altString = altBuffer.toString();
                    //替换存在冗余的字段
                    String vcfGeneReplace = vcfGene.get(i).replace(ref, refString);
                    vcfGeneReplace = vcfGeneReplace.replace(alt, altString);
                    //System.out.println("repalce -"+vcfGeneReplace);
                                       
                    writer.write(vcfGeneReplace+"\n");                    
                }
                else { 
                    //if(ref.charAt(0) == alt.charAt(0)){
                    //    System.out.println("repalce -"+vcfGene.get(i));
                    //}
                    
                    writer.write(vcfGene.get(i)+"\n");                    
                }                                
            }                            
        }                   
        writer.close();       
    }            
}
    