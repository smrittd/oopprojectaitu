public class LibraryMember {
    private String name;
    private String memberId;

    public LibraryMember(String name, String memberId){
        this.name = name;
        this.memberId = memberId;
    }


    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getMemberId(){return memberId;}
    public void setMemberId(String memberId){this.memberId = memberId;}


    public void displayInfo(){
        System.out.println("Member: " + name + " (ID: " + memberId + ")");
    }
}
