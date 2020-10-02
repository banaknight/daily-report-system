package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "worktimes")
@NamedQueries({
    @NamedQuery(
            name = "getAllWorktimes",
            query = "SELECT w FROM Worktime AS w ORDER BY w.id DESC"
            ),
    @NamedQuery(
            name = "getWorktimesCount",
            query = "SELECT COUNT(w) FROM Worktime AS w"
            ),
})

@NamedNativeQueries({
    @NamedNativeQuery(
            name = "getStart_at",
            query = "SELECT * FROM Worktimes WHERE employee_id = :employee AND DATE(start_at) = DATE(:start_at)",
            resultClass = Worktime.class
            ),
})
@Entity

public class Worktime {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_at")
    private Timestamp start_at;


    @Column(name = "end_at")
    private Timestamp end_at;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Employee getEmployee() {
        return employee;
    }


    public void setEmployee(Employee employee) {
        this.employee = employee;
    }


    public Timestamp getStart_at() {
        return start_at;
    }


    public void setStart_at(Timestamp start_at) {
        this.start_at = start_at;
    }


    public Timestamp getEnd_at() {
        return end_at;
    }


    public void setEnd_at(Timestamp end_at) {
        this.end_at = end_at;
    }


}
