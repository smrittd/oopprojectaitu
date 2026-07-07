public class LibraryMember {
    private String name;
    private String memberId;

    public LibraryMember(String name, String memberId){
        this.name = name;
        this.memberId = memberId;
    }


    public String getName(){return name;}
    public void setName(){this.name = name;}

    public String getMemberId(){return memberId;}
    public void setMemberId(){this.memberId = memberId;}


    public void displayInfo(){
        System.out.println("Member: " + name + " (ID: " + memberId + ")");
    }
}
