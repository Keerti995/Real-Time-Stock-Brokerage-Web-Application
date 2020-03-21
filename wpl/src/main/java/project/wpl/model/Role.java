package project.wpl.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "public")
public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Set<UserRegistry> users;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    public Set<UserRegistry> getUsers() {
        return users;
    }

    public void setUsers(Set<UserRegistry> users) {
        this.users = users;
    }

}
