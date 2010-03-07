
package frec.util;

import java.util.*;
import frec.core.GenetixFunction;

public final class GenList extends AbstractCollection
{
    private GenetixFunction[] elems; 
    private int size;
    private int inc;
    private int init;
    
    public GenList() 
    {
        elems = new GenetixFunction[10];
        init = 10;
        size = 0;
        inc = 5;
    }
    
    public GenList(int initialCapacity, int increment) 
    {
        this.init = initialCapacity;
        this.inc = increment;
        elems = new GenetixFunction[init];
        size = 0;
    }
    
    public boolean add(Object o)
    {
        if (o instanceof GenetixFunction)
        {
            if (size == elems.length)
            {
                GenetixFunction[] temp = 
                    new GenetixFunction[size+inc];
                System.arraycopy(elems, 0, temp, 0, size);
                elems = temp;
            }
            elems[size++] = (GenetixFunction)o;
            return true;
        }
        return false;
    } 
    
    public boolean addAll(Collection collection)
    {
        int siz = collection.size();
        GenetixFunction[] coll = new GenetixFunction[siz];
        collection.toArray(coll);
        GenetixFunction[] temp = new GenetixFunction[size+siz];
        System.arraycopy(elems, 0, temp, 0, size);
        System.arraycopy(coll, 0, temp, size, siz);
        elems = temp;
        size = size + siz;
        return true;       
    }
    
    public void addAll(GenetixFunction[] array)
    {
        int len = array.length;
        GenetixFunction[] temp = new GenetixFunction[size+len];
        System.arraycopy(elems, 0, temp, 0, size);
        System.arraycopy(array, 0, temp, size, len);
        elems = temp;
        size = size + len;
    }    
    
    public Object get(int index) 
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return elems[index];
    }
    
    public Object set(int index, Object element) 
    {
        Object res = null;
        if (element instanceof GenetixFunction)
        {
            if (index < size) 
            {
                res = elems[index];
                elems[index] = (GenetixFunction)element;
            }
            else throw new IndexOutOfBoundsException();
        }
        return res;
    }
    
    public Object remove(int index)
    {
        if (index < size)
        {
            Object res = elems[index];
            GenetixFunction[] temp = new GenetixFunction[size-1];
            System.arraycopy(elems, 0, temp, 0, index);
            System.arraycopy(elems, index+1, temp, index, size-index-1);
            elems = temp;
            size--;
            return res;
        }
        else throw new IndexOutOfBoundsException();
    }
    
    public int size() 
    {
        return size;
    }
    
    public boolean isEmpty() 
    {
	return size == 0;
    }    
    
    public void clear()
    {
        elems = new GenetixFunction[init];
        size = 0;
    }
    
    public boolean contains(Object o)
    {
        if (o instanceof GenetixFunction)
            for (int i=0; i<size; i++)
                if (elems[i].equals(o)) return true;    
        
        return false;
    }

    public Object[] toArray(Object array[]) 
    {
        if (array instanceof GenetixFunction[])
        {
            if (size==elems.length) return elems;
            GenetixFunction[] temp = new GenetixFunction[size];
            System.arraycopy(elems, 0, temp, 0, size);
            elems = temp;
            return temp;
        }
        else return super.toArray(array);
    }
    
    public Iterator iterator() 
    {
        return new GLIterator();
    }
    
    private class GLIterator implements Iterator 
    {
	/**
	 * Index of element to be returned by subsequent call to next.
	 */
	int index = 0;

	/**
	 * Index of element returned by most recent call to next or
	 * previous.  Reset to -1 if this element is deleted by a call
	 * to remove.
	 */
	int last = -1;

	public boolean hasNext() 
        {
	    return index != size;
	}

	public Object next() 
        {
            Object next = 
                GenList.this.get(index);
            last = index++;
            return next;
	}

	public void remove() 
        {
	    if (last == -1)
		throw new IllegalStateException();
            
            GenList.this.remove(last);
            if (last < index) index--;
            last = -1;
	}
    }
    
}
