
package frec.core;

import frec.util.*;
import java.util.Arrays;

/**
 *
 * @author  kares@zoznam.sk
 */
public class MyModelGenetix extends GPModelGenetix
{    
    public MyModelGenetix(){ }
    
    public MyModelGenetix(char[] symbol, byte[] arity){ }  
    
    private int init_size = 4;
    
    public void run()
    {
        int sel_size = 0;
        int size = gen_size / 10;
        if (size==0) size = 1;
        int counter = 0;
        
        System.out.println("init0");
        
        // advanced initialization:
        while (gen_list.size() < gen_size)
        {
            System.out.println("size = "+gen_list.size());
            if (Generator.randomBoolean())
                initializeGeneration(); //GP ~ Genetix
            else
                initializeGeneration(
                    init_size + (sel_size % 3)); //Genetix
            
            sel_size = 1 + Generator.randomInt(10);
            
            checkFitnessErrors();
            if (gen.length < size)
                if (counter++ < 100) continue;
            counter = 0;
            selectBest(sel_size);
            gen_list.addAll(gen);
            System.out.println("init1");
        }
        
        System.out.println("init2");
        
        while ((size = gen_list.size()) > gen_size)
            gen_list.remove(Generator.randomInt(size));
        
        gen = new GenetixFunction[gen_size];
        gen = (GenetixFunction[])gen_list.toArray(gen);
        
        System.out.println("start");
        
        if (isSaving)
            while (genCounter < maxCounter)
            {
                saveGeneration();
                System.out.println("1");
                sel_size = Math.round((float)gen_size 
                    * getSelectionProbability());
                System.out.println("2");
                selectBest(sel_size);
                GenetixFunction.setFunctionCodeLengthLimits(
                    ++adv_min, adv_max *= 2);                
                System.out.println("3");
                mutateGeneration(); //GP
                reproductGeneration(); //GP
                System.out.println("4");
                crossGeneration(); //GP
                System.out.println("5");
                GenetixFunction.setFunctionCodeLengthLimits(
                    --adv_min, adv_max /= 2);                
                gen = new GenetixFunction[gen_list.size()];
                System.out.println("6");
                gen_list = selectValid(
                    (GenetixFunction[])gen_list.toArray(gen));
                gen = (GenetixFunction[])gen_list.toArray(gen);
                gen_list.clear();
                System.out.println("7");
                checkPopulationErrors();
                System.out.println("8");
                while (gen.length < gen_size)
                {
                    this.addNewToGeneration(gen_size-gen.length);
                    checkFitnessErrors();
                }
                System.out.println("9");
                selectBest(gen_size);
                genCounter++;
            }            
        else
            while (genCounter < maxCounter)
            {
                sel_size = Math.round((float)gen_size 
                    * getSelectionProbability());
                selectBest(sel_size);
                GenetixFunction.setFunctionCodeLengthLimits(
                    ++adv_min, adv_max *= 2);                
                mutateGeneration(); //GP
                reproductGeneration(); //GP
                crossGeneration(); //GP
                GenetixFunction.setFunctionCodeLengthLimits(
                    --adv_min, adv_max /= 2);                
                gen = new GenetixFunction[gen_list.size()];
                gen_list = selectValid(
                    (GenetixFunction[])gen_list.toArray(gen));
                gen = (GenetixFunction[])gen_list.toArray(gen);
                gen_list.clear();
                checkPopulationErrors();
                while (gen.length < gen_size)
                {
                    this.addNewToGeneration(gen_size-gen.length);
                    checkFitnessErrors();
                }
                selectBest(gen_size);
                genCounter++;
            }

        hasFinished = true;
    }
    
    protected synchronized void crossGeneration()
    {
        int len = gen.length;
        float prob = getCrossingProbability();
        for (int i=0; i<gen.length; i++)
            if (Generator.randomBoolean(prob * (1 - i/gen.length)))
            {
                int rnd = Generator.ascRandomInt(len);
                while (rnd == i) rnd = Generator.ascRandomInt(len);
                
                GenetixFunction new1 = gen[i];
                GenetixFunction new2 = gen[rnd];
                GenetixFunction[] crossed = new GenetixFunction[
                    (new1.length()-1)*(new2.length()-1)];
                
                int index = 0;
                for (int pos1=1; pos1<new1.length(); pos1++)
                    for (int pos2=1; pos2<new2.length(); pos2++)
                    {
                        new1.crossFunctions(new2, pos1, pos2);
                        crossed[index++] = new1;
                        crossed[index++] = new2;
                        new1 = gen[i];
                        new2 = gen[rnd];
                    }
                
                gen_list.addAll(selectValid(crossed));
                
            }          
    } 
    
    private GenList selectValid(GenetixFunction[] funxs)
    {
        validate(funxs);
        GenList res = new GenList(funxs.length, 0);
        for (int i=0; i<funxs.length; i++)
            if (funxs[i].isValid()) res.add(funxs[i]);
        return res;
    }
    
}
