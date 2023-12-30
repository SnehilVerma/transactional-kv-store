import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TransactionalKV {

    class Transaction{
        Map<Integer,Integer> local;
        public Transaction(){
            local = new HashMap<>();
        }

    }
    Stack<Transaction> st = new Stack<>();
    Map<Integer,Integer> mp = new HashMap<>();

    public void set(int k,int v){
        if(st.isEmpty()){
            //no active txn, set in global store.
            mp.put(k,v);
            System.out.println("global "  + mp.toString());
        }else{
            Transaction top = st.peek();
            top.local.put(k,v); //add to local kv
            System.out.println("txn local " + st.peek().local.toString());
        }

    }

    public int get(int k){
        if(st.isEmpty()){
            return mp.getOrDefault(k, -1);  //no active txn, get from global store.
        }else{
            Transaction top = st.peek();
            return top.local.getOrDefault(k,-1);    //get from active txn.
        }

    }

    public boolean delete(int k){
        if(mp.containsKey(k)){
            mp.remove(k);
            return true;
        }else {
            return false;
        }
    }

    public void beginTxn(){
        if(st.isEmpty()){
            Transaction t = new Transaction();
            st.add(t);
        }else{
            Transaction top = st.peek();
            Transaction t = new Transaction();
            t.local = new HashMap<>(top.local); //get parent context.
            st.add(t);
        }

    }
    public void commitTxn(){
        if(st.isEmpty()){
            System.out.println("noting to commit");
        }else{
            Transaction top = st.pop();
            mp.putAll(top.local);   //copy all local map kv to global kv.
            System.out.println("global "  + mp.toString());

        }

    }
    public void rollbackTxn(){
        if(st.isEmpty()) {
            System.out.println("noting to rollback");
        }else{
            Transaction t = st.peek();
            t.local.clear();    //reset local map.
        }

    }
    public static void main(String[] args) {
        TransactionalKV transactionalKV = new TransactionalKV();
        transactionalKV.set(1,1);
        transactionalKV.set(2,2);
        System.out.println("get res " + transactionalKV.get(2));
        transactionalKV.beginTxn();
        transactionalKV.set(1,5);
        transactionalKV.beginTxn();
        transactionalKV.set(3,6);
        System.out.println("get res " + transactionalKV.get(2));
        System.out.println("get res " + transactionalKV.get(1));
        transactionalKV.commitTxn();
        transactionalKV.set(2,8);
        System.out.println("get res " + transactionalKV.get(2));
        transactionalKV.commitTxn();
        System.out.println("get res " + transactionalKV.get(2));
    }
}


