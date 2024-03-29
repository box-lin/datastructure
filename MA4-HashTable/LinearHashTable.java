
/*
 *  Microassignment: Probing Hash Table addElement and removeElement
 *
 *  LinearHashTable: Yet another Hash Table Implementation
 * 
 *  Copyright:
 *   For academic use only under the Creative Commons
 *   Attribution-NonCommercial-NoDerivatives 4.0 International License
 *   http://creativecommons.org/licenses/by-nc-nd/4.0
 */


class LinearHashTable<K, V> extends HashTableBase<K, V>
{
	// Linear and Quadratic probing should rehash at a load factor of 0.5 or higher
    private static final double REHASH_LOAD_FACTOR = 0.5;

    // Constructors
    public LinearHashTable()
    {
        super();
    }

    public LinearHashTable(HasherBase<K> hasher)
    {
        super(hasher);
    }

    public LinearHashTable(HasherBase<K> hasher, int number_of_elements)
    {
        super(hasher, number_of_elements);
    }

    // Copy constructor
    public LinearHashTable(LinearHashTable<K, V> other)
    {
        super(other);
	}
    
   
    // ***** MA Section Start ************************************************ //

    // Concrete implementation for parent's addElement method
    public void addElement(K key, V value)
    {
    	 // MA TODO: find empty slot to insert (update HashItem as necessary)
    	// Check for size restrictions
        resizeCheck();

        // Calculate hashkey based on data key
        int hash = super.getHash(key);

        //find the locaiton of the HashItem from the hashtable 
        HashItem<K,V> slot = _items.get(hash);
       
        //we keep probing if key not equal and slot not true empty, we will have 3 cases below when we quite the while loop
        while(!slot.isTrueEmpty() && slot.getKey() != key) {  
            hash = (hash+1)%_items.size(); //linear probing
        	slot = _items.get(hash);
        
        }
        
        //case 1: slot exist and key equals and we just update the value
        if(slot.getKey() == key && !slot.isEmpty()) {
        	slot.setValue(value);
        	//do not ++size because slot exist just the value changed
        }
        //case 2: slot marked deleted, but key equals then we update the value and mark it exist
        else if(slot.getKey() == key && slot.isEmpty()) {
        	slot.setValue(value);
        	slot.setIsEmpty(false);//in case the slot previously marked deleted.
        	_number_of_elements++; //++size because we just marked it exists
        }
        //case 3: slot is truly empty
        else if(slot.isTrueEmpty()) {
        	slot.setKey(key); 
        	slot.setValue(value);
        	slot.setIsEmpty(false);
        	_number_of_elements++;//++size because we marked it exists
        }

    }

    // Removes supplied key from hash table
    public void removeElement(K key)
    {
        // Calculate hash from key
        int hash = super.getHash(key);
        
        
        // MA TODO: find slot to remove. Remember to check for infinite loop!
        //  ALSO: Use lazy deletion - see structure of HashItem
 
        HashItem<K,V> slot = _items.get(hash);
        
        //if slot is truly empty and key not equal, we keep probing.
        //case that went out the while loop: key equals and not true empty:
        while(slot.getKey() != key && !slot.isTrueEmpty()) {  
        	hash = (hash+1)%_items.size(); //linear probing
        	slot = _items.get(hash);
        }
        
        //when key equals, the slot might be marked exist or deleted.
        //our purpose is remove, so we mark exist to deleted.
        if(slot.getKey() == key && !slot.isEmpty()) {
        	slot.setIsEmpty(true);//we want only mark it as deleted.
        	_number_of_elements--;
        }

    }
    
    // ***** MA Section End ************************************************ //
    

    // Public API to get current number of elements in Hash Table
	public int size() {
		return this._number_of_elements;
	}

    // Public API to test whether the Hash Table is empty (N == 0)
	public boolean isEmpty() {
		return this._number_of_elements == 0;
	}
    
    // Returns true if the key is contained in the hash table
    public boolean containsElement(K key)
    {
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(hash);
        
        // Left incomplete to avoid hints in the MA :)
        return false;
    }
    
    // Returns the item pointed to by key
    public V getElement(K key)
    {
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(hash);
        
        // Left incomplete to avoid hints in the MA :)
        return null;
    }

    // Determines whether or not we need to resize
    //  to turn off resize, just always return false
    protected boolean needsResize()
    {
        // Linear probing seems to get worse after a load factor of about 50%
        if (_number_of_elements > (REHASH_LOAD_FACTOR * _primes[_local_prime_index]))
        {
            return true;
        }
        return false;
    }
    
    // Called to do a resize as needed
    protected void resizeCheck()
    {
        // Right now, resize when load factor > 0.5; it might be worth it to experiment with 
        //  this value for different kinds of hashtables
        if (needsResize())
        {
            _local_prime_index++;

            HasherBase<K> hasher = _hasher;
            LinearHashTable<K, V> new_hash = new LinearHashTable<K, V>(hasher, _primes[_local_prime_index]);

            for (HashItem<K, V> item: _items)
            {
                if (item.isEmpty() == false)
                {
                    // Add element to new hash table
                    new_hash.addElement(item.getKey(), item.getValue());
                }
            }

            // Steal temp hash object's internal vector for ourselves
            _items = new_hash._items;
        }
    }

    // Debugging tool to print out the entire contents of the hash table
	public void printOut() {
		System.out.println(" Dumping hash with " + _number_of_elements + " items in " + _items.size() + " buckets");
		System.out.println("[X] Key	| Value	| Deleted");
		for( int i = 0; i < _items.size(); i++ ) {
			HashItem<K, V> curr_slot = _items.get(i);
			System.out.print("[" + i + "] ");
			System.out.println(curr_slot.getKey() + " | " + curr_slot.getValue() + " | " + curr_slot.isEmpty());
		}
	}
}