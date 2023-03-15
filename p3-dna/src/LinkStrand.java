public class LinkStrand implements IDnaStrand {

    private class Node {
        private String info;
        private Node next;

        public Node (String info) {
            this.info = info;
        }
    }

    private Node myFirst, myLast;
    private long mySize;
    private int myAppends;
    private int myIndex;
    private Node myCurrent;
    private int myLocalIndex;

    public LinkStrand (String source) {
        initialize(source);
    }

    public LinkStrand () {
        this("");
    }

    @Override
    public long size() {
        return mySize;
    }

    @Override
    public void initialize(String source) {
        myFirst = new Node(source);
        myLast = myFirst;
        mySize = source.length();
        myAppends = 0;
        myIndex = 0;
        myCurrent = myFirst;
        myLocalIndex = 0;
    }

    @Override
    public IDnaStrand getInstance(String source) {
        return new LinkStrand(source);
    }

    @Override
    public IDnaStrand append(String dna) {
        myLast.next = new Node(dna);
        myLast = myLast.next;
        mySize += dna.length();
        myAppends++;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node pointer = myFirst;

        while (pointer != null) {
            result.append(pointer.info);
            pointer = pointer.next;
        }

        return result.toString();
    }

    @Override
    public IDnaStrand reverse() {
        String[] nodeList = this.toStringSpace().split(" ");
        LinkStrand result = new LinkStrand(nodeList[0]);

        for (int i = 1; i < nodeList.length; i++) {
            result.append(nodeList[i]);
        }

        return result;
    }

    private String toStringSpace() {
        StringBuilder result = new StringBuilder();
        Node pointer = myFirst;

        while (pointer.next != null) {
            StringBuilder temp = new StringBuilder(pointer.info);
            temp.append(" ");
            result.append(temp);
            pointer = pointer.next;
        }
        StringBuilder temp = new StringBuilder(pointer.info);
        result.append(temp);
        result = result.reverse();

        return result.toString();
    }

    @Override
    public int getAppendCount() {
        return myAppends;
    }

    @Override
    public char charAt (int index) {
        if (index < 0 || index >= mySize) {
            throw new IndexOutOfBoundsException();
        }
        else if (myIndex > index) {
            myIndex = 0;
            myLocalIndex = 0;
            myCurrent = myFirst;
        }
        while (myIndex < index) {
            myIndex++;
            myLocalIndex++;
            if (myCurrent.info.length() == myLocalIndex) {
                myLocalIndex = 0;
                myCurrent = myCurrent.next;
            }
        }
        return myCurrent.info.charAt(myLocalIndex);
    }
}
