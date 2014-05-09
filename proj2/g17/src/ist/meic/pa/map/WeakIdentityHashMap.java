package ist.meic.pa.map;

/*
 *   Copyright (C) Christian Schulte, 2005-206
 *   All rights reserved.
 */

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


public final class WeakIdentityHashMap<K, V> implements Map<K, V> {

    /** Maximum capacity of the hash-table backing the implementation ({@code 2^30}). */
    private static final int MAXIMUM_CAPACITY = 0x40000000;

    /** Default initial capacity ({@code 2^4}). */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** Default load factor ({@code 0.75}). */
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;

    /** The number of times the map got structurally modified. */
    private int modifications;

    /** The number of mappings held by the map. */
    private int size;

    /** The size value at which the hash table needs resizing. */
    private int resizeThreshold;

    /** The hash-table backing the map. */
    private WeakEntry<K, V>[] hashTable;

    /** Queue, to which weak keys are appended to. */
    private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<K>();

    /** The key set view of the map. */
    private transient Set<K> keySet;

    /** The entry set view of the map. */
    private transient Set<Map.Entry<K, V>> entrySet;

    /** The value collection view of the map. */
    private transient Collection<V> valueCollection;

    /** The initial capacity of the hash table. */
    private final int initialCapacity;

    /** The load factor for the hash table. */
    private final float loadFactor;

    /** Null value returned by method {@link #getEntry(Object)}. */
    private final WeakEntry<K, V> NULL_ENTRY = new WeakEntry<K, V>( null, null, 0, this.referenceQueue );

    /**
     * Constructs a new, empty {@code WeakIdentityHashMap} with the default initial capacity ({@code 16}) and load
     * factor ({@code 0.75}).
     */
    public WeakIdentityHashMap()
    {
        this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR );
    }

    /**
     * Constructs a new, empty {@code WeakIdentityHashMap} with the given initial capacity and the default load factor
     * ({@code 0.75}).
     *
     * @param  initialCapacity The initial capacity of the {@code WeakIdentityHashMap}.
     *
     * @throws IllegalArgumentException if {@code initialCapacity} is negative or greater than the maximum supported
     * capacity ({@code 2^30}).
     */
    public WeakIdentityHashMap( final int initialCapacity )
    {
        this( initialCapacity, DEFAULT_LOAD_FACTOR );
    }

    /**
     * Constructs a new, empty {@code WeakIdentityHashMap} with the default initial capacity ({@code 16}) and the given
     * load factor.
     *
     * @param loadFactor The load factor of the {@code WeakIdentityHashMap}.
     *
     * @throws IllegalArgumentException if {@code loadFactor} is nonpositive.
     */
    public WeakIdentityHashMap( final float loadFactor )
    {
        this( DEFAULT_INITIAL_CAPACITY, loadFactor );
    }

    /**
     * Constructs a new, empty {@code WeakIdentityHashMap} with the given initial capacity and the given load factor.
     *
     * @param initialCapacity The initial capacity of the {@code WeakIdentityHashMap}.
     * @param loadFactor The load factor of the {@code WeakIdentityHashMap}.
     *
     * @throws IllegalArgumentException if {@code initialCapacity} is negative or greater than the maximum supported
     * capacity ({@code 2^30}), or if {@code loadFactor} is nonpositive.
     */
    public WeakIdentityHashMap( final int initialCapacity, final float loadFactor )
    {
        if ( initialCapacity < 0 || initialCapacity > MAXIMUM_CAPACITY )
        {
            throw new IllegalArgumentException( Integer.toString( initialCapacity ) );
        }
        if ( loadFactor <= 0 || Float.isNaN( loadFactor ) )
        {
            throw new IllegalArgumentException( Float.toString( loadFactor ) );
        }

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.resizeThreshold = initialCapacity;
        this.size = 0;
        this.hashTable = new WeakEntry[ initialCapacity ];
    }

    /**
     * Gets the number of key-value mappings in this map.
     * <p>If the map contains more than {@code Integer.MAX_VALUE} elements, returns {@code Integer.MAX_VALUE}.</p>
     *
     * @return The number of key-value mappings in this map.
     */
    public int size()
    {
        if ( this.size > 0 )
        {
            this.purge();
        }

        return this.size;
    }

    /**
     * Gets a flag indicating if this map is empty.
     *
     * @return {@code true}, if this map contains no key-value mappings; {@code false}, if this map contains at least
     * one mapping.
     */
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    /**
     * Gets a flag indicating if this map contains a mapping for a given key.
     * <p>More formally, returns {@code true}, if and only if this map contains a mapping for a key {@code k} such that
     * {@code key==k}. There can be at most one such mapping.</p>
     *
     * @param key The key whose presence in this map is to be tested.
     *
     * @return {@code true}, if this map contains a mapping for {@code key}; {@code false}, if this map does not contain
     * a mapping for {@code key}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    public boolean containsKey( final Object key )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return this.getEntry( key ).value != null;
    }

    /**
     * Gets a flag indicating if this map maps one or more keys to the specified value.
     * <p>More formally, this method returns {@code true}, if and only if this map contains at least one mapping to a
     * value {@code v} such that {@code value.equals(v)}. This operation requires time linear in the map size.</p>
     *
     * @param value The value whose presence in this map is to be tested.
     *
     * @return {@code true}, if this map maps one or more keys to {@code value}; {@code false}, if this map does not map
     * any key to {@code value}.
     *
     * @throws NullPointerException if {@code value} is {@code null}.
     */
    public boolean containsValue( final Object value )
    {
        if ( value == null )
        {
            throw new NullPointerException( "value" );
        }

        final WeakEntry<K, V>[] table = this.getHashTable();

        for ( int i = table.length - 1; i >= 0; i-- )
        {
            for ( WeakEntry<K, V> e = table[i]; e != null; e = e.next )
            {
                if ( value.equals( e.value ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the value to which a given key is mapped or {@code null}, if this map contains no mapping for that key.
     * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that
     * {@code key==k}, then this method returns {@code v}; otherwise it returns {@code null}. There can be at most one
     * such mapping.</p>
     *
     * @param key The key whose associated value is to be returned.
     *
     * @return The value to which {@code key} is mapped or {@code null}, if this map contains no mapping for
     * {@code key}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    public V get( final Object key )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return this.getEntry( key ).value;
    }

    /**
     * Associates a given value with a given key in this map.
     * <p>If the map previously contained a mapping for that key, the old value is replaced by the given value.</p>
     *
     * @param key The key with which {@code value} is to be associated.
     * @param value The value to be associated with {@code key}.
     *
     * @return The value previously associated with {@code key} or {@code null}, if there was no mapping for
     * {@code key}.
     *
     * @throws NullPointerException if {@code key} or {@code value} is {@code null}.
     */
    public V put( final K key, final V value )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }
        if ( value == null )
        {
            throw new NullPointerException( "value" );
        }

        final int hashCode = System.identityHashCode( key );
        final WeakEntry<K, V>[] table = this.getHashTable();
        final int index = getHashTableIndex( hashCode, table.length );

        for ( WeakEntry<K, V> e = table[index]; e != null; e = e.next )
        {
            if ( e.hashCode == hashCode && e.get() == key )
            {
                final V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        final WeakEntry<K, V> entry = new WeakEntry<K, V>( key, value, hashCode, this.referenceQueue );
        entry.next = table[index];
        table[index] = entry;

        this.increaseSize();

        return null;
    }

    /**
     * Removes the mapping for a given key from this map if it is present.
     * <p>More formally, if this map contains a mapping from key {@code k} to value {@code v} such that {@code key==k},
     * that mapping is removed. The map can contain at most one such mapping. The map will not contain a mapping for the
     * given key once the call returns.</p>
     *
     * @param key The key whose mapping is to be removed from the map.
     *
     * @return The value previously associated with {@code key} or {@code null}, if there was no mapping for
     * {@code key}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    public V remove( final Object key )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        final WeakEntry<K, V>[] table = this.getHashTable();
        final int hashCode = System.identityHashCode( key );
        final int index = getHashTableIndex( hashCode, table.length );

        for ( WeakEntry<K, V> e = table[index], pre = null; e != null; pre = e, e = e.next )
        {
            if ( e.hashCode == hashCode && e.get() == key )
            {
                if ( pre != null )
                {
                    pre.next = e.next;
                }
                else
                {
                    table[index] = e.next;
                }

                this.decreaseSize();

                final V removed = e.value;
                e.removed = true;
                e.value = null;
                e.next = null;
                return removed;
            }
        }

        return null;
    }

    /**
     * Copies all of the mappings from a given map to this map.
     * <p>The effect of this call is equivalent to that of calling {@link #put(Object,Object) put(k, v)} on this map
     * once for each mapping from key {@code k} to value {@code v} in the given map. The behavior of this operation is
     * undefined if the given map is modified while the operation is in progress.</p>
     *
     * @param m The mappings to be stored in this map.
     *
     * @throws NullPointerException if {@code map} is {@code null}, or if {@code map} contains {@code null} keys or
     * values.
     */
    public void putAll( final Map<? extends K, ? extends V> m )
    {
        if ( m == null )
        {
            throw new NullPointerException( "m" );
        }

        for ( final Iterator<? extends Map.Entry<? extends K, ? extends V>> it = m.entrySet().iterator();
              it.hasNext(); )
        {
            final Map.Entry<? extends K, ? extends V> entry = it.next();
            this.put( entry.getKey(), entry.getValue() );
        }
    }

    /** Removes all of the mappings from this map so that the map will be empty after this call returns. */
    @SuppressWarnings( "empty-statement" )
    public void clear()
    {
        this.purge();
        this.hashTable = new WeakEntry[ this.initialCapacity ];
        this.size = 0;
        this.resizeThreshold = this.initialCapacity;
        this.modifications++;
        while ( this.referenceQueue.poll() != null );
    }

    /**
     * Gets a {@code Set} view of the keys contained in this map.
     * <p>The set is backed by the map, so changes to the map are reflected in the set, and vice-versa. If the map is
     * modified while an iteration over the set is in progress (except through the iterator's own {@code remove}
     * operation), the results of the iteration are undefined, that is, the iterator may throw an
     * {@code IllegalStateException}. The set supports element removal, which removes the corresponding mapping from the
     * map, via the {@code Iterator.remove}, {@code Set.remove}, {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations. It does not support the {@code add} or {@code addAll} operations.</p>
     *
     * @return A set view of the keys contained in this map.
     */
    public Set<K> keySet()
    {
        if ( this.keySet == null )
        {
            this.keySet = new AbstractSet<K>()
            {

                public Iterator<K> iterator()
                {
                    return new KeyIterator();
                }

                public int size()
                {
                    return WeakIdentityHashMap.this.size();
                }

            };

        }

        return this.keySet;
    }

    /**
     * Gets a {@code Collection} view of the values contained in this map.
     * <p>The collection is backed by the map, so changes to the map are reflected in the collection, and vice-versa.
     * If the map is modified while an iteration over the collection is in progress (except through the iterator's own
     * {@code remove} operation), the results of the iteration are undefined, that is, the iterator may throw an
     * {@code IllegalStateException}. The collection supports element removal, which removes the corresponding mapping
     * from the map, via the {@code Iterator.remove}, {@code Collection.remove}, {@code removeAll}, {@code retainAll}
     * and {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.</p>
     *
     * @return A collection view of the values contained in this map.
     */
    public Collection<V> values()
    {
        if ( this.valueCollection == null )
        {
            this.valueCollection = new AbstractCollection<V>()
            {

                public Iterator<V> iterator()
                {
                    return new ValueIterator();
                }

                public int size()
                {
                    return WeakIdentityHashMap.this.size();
                }

            };
        }

        return this.valueCollection;
    }

    /**
     * Gets a {@code Set} view of the mappings contained in this map.
     * <p>The set is backed by the map, so changes to the map are reflected in the set, and vice-versa. If the map is
     * modified while an iteration over the set is in progress (except through the iterator's own {@code remove}
     * operation, or through the {@code setValue} operation on a map entry returned by the iterator) the results of the
     * iteration are undefined, that is, the iterator may throw an {@code IllegalStateException}. The set supports
     * element removal, which removes the corresponding mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and {@code clear} operations. It does not support the
     * {@code add} or {@code addAll} operations.</p>
     *
     * @return A set view of the mappings contained in this map.
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
        if ( this.entrySet == null )
        {
            this.entrySet = new AbstractSet<Map.Entry<K, V>>()
            {

                public Iterator<Map.Entry<K, V>> iterator()
                {
                    return new EntryIterator();
                }

                public int size()
                {
                    return WeakIdentityHashMap.this.size();
                }

            };
        }

        return this.entrySet;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return super.toString() + this.internalString();
    }

    /**
     * Compares the specified object with this map for equality.
     * <p>Returns {@code true}, if the given object is also a map and the two maps represent the same mappings. More
     * formally, two maps {@code m1} and {@code m2} represent the same mappings if
     * {@code m1.entrySet().equals(m2.entrySet())}.</p>
     *
     * @param o The object to be compared for equality with this map.
     *
     * @return {@code true}, if {@code o} is equal to this map; {@code false}, if {@code o} is not equal to this map.
     */
    @Override
    public boolean equals( final Object o )
    {
        boolean equal = this == o;

        if ( !equal && o instanceof Map<?, ?> )
        {
            final Map<?, ?> that = (Map<?, ?>) o;
            equal = this.entrySet().equals( that.entrySet() );
        }

        return equal;
    }

    /**
     * Gets the hash code value for this map.
     * <p>The hash code of a map is defined to be the sum of the hash codes of each entry in the map's
     * {@code entrySet()} view.</p>
     *
     * @return The hash code value for this map.
     */
    @Override
    public int hashCode()
    {
        return this.entrySet().hashCode();
    }

    /**
     * Finalizes the object by polling the internal reference queue for any pending references.
     *
     * @since 1.2
     */
    @Override
    protected void finalize() throws Throwable
    {
        this.modifications++;
        while ( this.referenceQueue.poll() != null );
        super.finalize();
    }

    /**
     * Creates a string representing the mappings of the instance.
     *
     * @return A string representing the mappings of the instance.
     */
    private String internalString()
    {
        final StringBuilder buf = new StringBuilder( 12 * this.size() ).append( '{' );
        final WeakEntry<K, V>[] table = this.getHashTable();
        for ( int i = table.length - 1, index = 0; i >= 0; i-- )
        {
            for ( WeakEntry<K, V> e = table[i]; e != null; e = e.next )
            {
                if ( buf.length() > 1 )
                {
                    buf.append( ", " );
                }

                buf.append( '[' ).append( index++ ).append( "]=" ).append( e );
            }
        }

        return buf.append( '}' ).toString();
    }

    /**
     * Gets the index of a hash code value.
     *
     * @param hashCode The hash code value to return the index of.
     * @param capacity The capacity to return an index for.
     *
     * @return The index of {@code hashCode} for {@code capacity}.
     */
    private static int getHashTableIndex( final int hashCode, final int capacity )
    {
        return hashCode & ( capacity - 1 );
    }

    /**
     * Gets the hash-table backing the instance.
     * <p>This method creates a new hash-table and re-hashes any mappings whenever the size of the map gets greater than
     * the capacity of the internal hash-table times the load factor value.</p>
     *
     * @return The hash-table backing the instance.
     */
    private WeakEntry<K, V>[] getHashTable()
    {
        if ( this.hashTable.length < this.resizeThreshold )
        {
            @SuppressWarnings( "unchecked" )
            final WeakEntry<K, V>[] table = new WeakEntry[ this.calculateCapacity() ];

            for ( int i = this.hashTable.length - 1; i >= 0; i-- )
            {
                WeakEntry<K, V> entry = this.hashTable[i];

                while ( entry != null )
                {
                    final WeakEntry<K, V> next = entry.next;
                    final int index = getHashTableIndex( entry.hashCode, table.length );

                    entry.next = table[index];
                    table[index] = entry;
                    entry = next;
                }
            }

            this.hashTable = table;
            this.modifications++;
        }

        this.purge();
        return this.hashTable;
    }

    /** Removes any garbage collected entries. */
    private void purge()
    {
        WeakEntry<K, V> purge;

        while ( ( purge = (WeakEntry<K, V>) this.referenceQueue.poll() ) != null )
        {
            final int index = getHashTableIndex( purge.hashCode, this.hashTable.length );

            for ( WeakEntry<K, V> e = this.hashTable[index], pre = null; e != null; pre = e, e = e.next )
            {
                if ( e == purge )
                {
                    if ( pre != null )
                    {
                        pre.next = purge.next;
                    }
                    else
                    {
                        this.hashTable[index] = purge.next;
                    }

                    purge.removed = true;
                    purge.next = null;
                    purge.value = null;

                    this.decreaseSize();

                    break;
                }
            }
        }
    }

    private void increaseSize()
    {
        if ( this.size < Integer.MAX_VALUE )
        {
            this.size++;
            this.resizeThreshold = (int) ( this.size * this.loadFactor );
        }

        this.modifications++;
    }

    private void decreaseSize()
    {
        if ( this.size > 0 )
        {
            this.size--;
        }

        this.modifications++;
    }

    private int calculateCapacity()
    {
        int maxCapacity = this.initialCapacity;
        if ( maxCapacity < this.resizeThreshold )
        {
            maxCapacity = this.resizeThreshold > MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : this.resizeThreshold;
        }

        int capacity = 1;
        while ( capacity < maxCapacity )
        {
            capacity <<= 1;
        }

        return capacity;
    }

    private WeakEntry<K, V> getEntry( final Object key )
    {
        final int hashCode = System.identityHashCode( key );
        final WeakEntry<K, V>[] table = getHashTable();

        for ( WeakEntry<K, V> e = table[getHashTableIndex( hashCode, table.length )]; e != null; e = e.next )
        {
            if ( e.hashCode == hashCode && e.get() == key )
            {
                return e;
            }
        }

        return NULL_ENTRY;
    }

    /**
     * A map entry (key-value pair) with weakly referenced key.
     * <p>The {@code WeakIdentityHashMap.entrySet} method returns a collection-view of the map, whose elements are of
     * this class. The only way to obtain a reference to a map entry is from the iterator of this collection-view. These
     * {@code Map.Entry} objects are valid only for the duration of the iteration; more formally, the behavior of a map
     * entry is undefined if the backing map has been modified after the entry was returned by the iterator, except
     * through the {@code setValue} operation on the map entry.</p>
     *
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     *
     * @see WeakIdentityHashMap#entrySet()
     */
    private static class WeakEntry<K, V> extends WeakReference<K> implements Map.Entry<K, V>
    {

        /** The value of the entry. */
        private V value;

        /** The next entry in the bucket. */
        private WeakEntry<K, V> next;

        /** Flag indicating that this entry got removed from the map. */
        private boolean removed;

        /** The hash code value of the key. */
        private final int hashCode;

        WeakEntry( final K key, final V value, final int hashCode, final ReferenceQueue<K> queue )
        {
            super( key, queue );
            this.hashCode = hashCode;
            this.value = value;
        }

        /**
         * Gets the key corresponding to this entry.
         *
         * @return The key corresponding to this entry.
         *
         * @throws IllegalStateException if the entry got removed from the backing map (either due to an iterator's
         * {@code remove} operation or due to the key having been garbage collected).
         */
        public K getKey()
        {
            final K key = this.get();

            if ( key == null || this.removed )
            {
                throw new IllegalStateException();
            }

            return key;
        }

        /**
         * Gets the value corresponding to this entry.
         *
         * @return The value corresponding to this entry.
         *
         * @throws IllegalStateException if the entry got removed from the backing map (either due to an iterator's
         * {@code remove} operation or due to the key having been garbage collected).
         */
        public V getValue()
        {
            if ( this.get() == null || this.removed )
            {
                throw new IllegalStateException();
            }

            return this.value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified value.
         *
         * @param value The new value to be stored in this entry.
         *
         * @return The old value corresponding to the entry.
         *
         * @throws NullPointerException if {@code value} is {@code null}.
         * @throws IllegalStateException if the entry got removed from the backing map (either due to an iterator's
         * {@code remove} operation or due to the key having been garbage collected).
         */
        public V setValue( final V value )
        {
            if ( value == null )
            {
                throw new NullPointerException( "value" );
            }
            if ( this.get() == null || this.removed )
            {
                throw new IllegalStateException();
            }

            final V oldValue = this.getValue();

            if ( value != oldValue && !value.equals( oldValue ) )
            {
                this.value = value;
            }

            return oldValue;
        }

        /**
         * Returns a string representation of the object.
         *
         * @return A string representation of the object.
         */
        @Override
        public String toString()
        {
            return super.toString() + this.internalString();
        }

        /**
         * Compares a given object with this entry for equality.
         * <p>Returns {@code true}, if the given object is also a map entry and the two entries represent the same
         * mapping. More formally, two entries {@code e1} and {@code e2} represent the same mapping if
         * <pre><blockquote>
         * ( e1.getKey() == e2.getKey() )  &amp;&amp;
         * ( e1.getValue().equals( e2.getValue() ) )
         * </blockquote></pre></p>
         *
         * @param o The object to be compared for equality with this map entry.
         *
         * @return {@code true}, if {@code o} is equal to this map entry; {@code false}, if {@code o} is not equal to
         * this map entry.
         */
        @Override
        public boolean equals( final Object o )
        {
            boolean equal = this == o;

            if ( !equal && o instanceof Map.Entry<?, ?> )
            {
                final Map.Entry<?, ?> that = (Map.Entry<?, ?>) o;
                equal = this.getKey() == that.getKey() && this.getValue().equals( that.getValue() );
            }

            return equal;
        }

        /**
         * Gets the hash code value for this map entry.
         * <p>The hash code of a map entry {@code e} is defined to be:
         * <pre><blockquote>
         * ( e.getKey() == null ? 0 : e.getKey().hashCode() ) ^
         * ( e.getValue() == null ? 0 : e.getValue().hashCode() )
         * </blockquote></pre></p>
         *
         * @return The hash code value for this map entry.
         */
        @Override
        public int hashCode()
        {
            return ( this.hashCode ) ^ ( this.getValue().hashCode() );
        }

        /**
         * Creates a string representing the properties of the instance.
         *
         * @return A string representing the properties of the instance.
         */
        private String internalString()
        {
            final StringBuilder buf = new StringBuilder( 50 ).append( '{' );
            buf.append( "key=" ).append( this.getKey() ).append( ", value=" ).append( this.getValue() );
            return buf.append( '}' ).toString();
        }

    }

    /** Base iterator implementation over the hash-table backing the implementation. */
    private class WeakEntryIterator
    {

        /** The next element in the iteration. */
        private WeakEntry<K, V> next;

        /** The current element in the iteration. */
        private WeakEntry<K, V> current;

        /** The current index into the hash-table. */
        private int index;

        /** The number of modifications when this iterator got created. */
        private int modifications;

        /** Creates a new {@code WeakEntryIterator} instance. */
        WeakEntryIterator()
        {
            final WeakEntry<K, V>[] table = getHashTable();
            for ( this.index = table.length - 1; this.index >= 0; this.index-- )
            {
                if ( table[this.index] != null )
                {
                    this.next = table[this.index--];
                    break;
                }
            }

            this.modifications = WeakIdentityHashMap.this.modifications;
        }

        /**
         * Gets a flag indicating that the iteration has more elements.
         *
         * @return {@code true}, if the iterator has more elements; {@code false}, if the iterator does not have more
         * elements.
         */
        public boolean hasNext()
        {
            if ( this.modifications != WeakIdentityHashMap.this.modifications )
            {
                throw new ConcurrentModificationException();
            }

            return this.next != null;
        }

        /**
         * Gets the next element in the iteration.
         *
         * @return The next element in the iteration.
         *
         * @throws NoSuchElementException if the iterator does not have more elements.
         */
        public Map.Entry<K, V> nextElement()
        {
            if ( this.modifications != WeakIdentityHashMap.this.modifications )
            {
                throw new ConcurrentModificationException();
            }
            if ( this.next == null )
            {
                throw new NoSuchElementException();
            }

            this.current = this.next;

            if ( this.next.next != null )
            {
                this.next = this.next.next;
            }
            else
            {
                this.next = null;
                final WeakEntry<K, V>[] table = getHashTable();
                for ( ; this.index >= 0; this.index-- )
                {
                    if ( table[this.index] != null )
                    {
                        this.next = table[this.index--];
                        break;
                    }
                }
            }

            return this.current;
        }

        /**
         * Removes from the underlying hash-table the last element returned by the iterator.
         *
         * @throws IllegalStateException if the {@code next} method has not yet been called, or the {@code remove}
         * method has already been called after the last call to the {@code next} method.
         */
        public void remove()
        {
            if ( this.modifications != WeakIdentityHashMap.this.modifications )
            {
                throw new ConcurrentModificationException();
            }
            if ( this.current == null )
            {
                throw new IllegalStateException();
            }

            final K key = this.current.getKey();

            if ( key == null )
            {
                throw new IllegalStateException();
            }

            WeakIdentityHashMap.this.remove( key );
            this.modifications = WeakIdentityHashMap.this.modifications;
            this.current = null;
        }

    }

    /** Iterator over the hash-table backing the implementation. */
    private class EntryIterator extends WeakEntryIterator implements Iterator<Map.Entry<K, V>>
    {

        /** Creates a new {@code EntryIterator} instance. */
        EntryIterator()
        {
            super();
        }

        /**
         * Gets the next element in the iteration.
         *
         * @return The next element in the iteration.
         *
         * @throws NoSuchElementException if the iterator does not have more elements.
         */
        public Map.Entry<K, V> next()
        {
            return super.nextElement();
        }

    }

    /** Iterator over the hash-table backing the implementation. */
    private class KeyIterator extends WeakEntryIterator implements Iterator<K>
    {

        /** Creates a new {@code KeyIterator} instance. */
        KeyIterator()
        {
            super();
        }

        /**
         * Gets the next element in the iteration.
         *
         * @return The next element in the iteration.
         *
         * @throws NoSuchElementException if the iterator does not have more elements.
         */
        public K next()
        {
            return super.nextElement().getKey();
        }

    }

    /** Iterator over the hash-table backing the implementation. */
    private class ValueIterator extends WeakEntryIterator implements Iterator<V>
    {

        /** Creates a new {@code ValueIterator} instance. */
        ValueIterator()
        {
            super();
        }

        /**
         * Gets the next element in the iteration.
         *
         * @return The next element in the iteration.
         *
         * @throws NoSuchElementException if the iterator does not have more elements.
         */
        public V next()
        {
            return super.nextElement().getValue();
        }

    }

}