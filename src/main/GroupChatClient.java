package main;

public class GroupChatClient {
    private int id;
    private String groupName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public GroupChatClient(int id, String groupName) {

        this.id = id;
        this.groupName = groupName;
    }
}
