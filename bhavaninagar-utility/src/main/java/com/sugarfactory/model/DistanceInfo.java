package com.sugarfactory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name="COM_WEIGHT_SLIP_DIST")
public class DistanceInfo
{
    @Id
    @SequenceGenerator(name="slip_dist_generator",sequenceName = "slip_dist_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "slip_dist_generator")
    private Long id;

    @Column(name = "VSEASON_YEAR")
    protected String yearCode;
    @Column(name = "NSLIP_NO")
    protected Integer slipNo;
    @Column(name = "NSHIFT_NO")
    private Integer shiftNumber;
    @Column(name = "DSLIP_DATE")
    private Date slipDate;
    @Column(name = "NPLOT_NO")
    private Integer plotNumber;
    @Column(name = "NTOKEN_NO")
    private Integer token;
    @Column(name = "NDISTANCE")
    private Integer distance;
    @Column(name = "NVEHICLE_TYPE")
    private Integer vehicleType;
    @Column(name = "NHARVESTER_CODE")
    private Integer harvestorCode;
    @Column(name = "NTRANSPORTER_CODE")
    private Integer transporterCode;
    @Column(name = "VVEHICLE_NO")
    private String vehicleNumber;
    @Column(name = "NGADIWAN_CODE")
    private Integer bulluckcartCode;
    @Column(name = "NBULLOCK_CART_CODE")
    private Integer bulluckcartMainCode;
    @Column(name = "DCREATE_DATE")
    private Date createDate;
    @Column(name = "DUPDATE_DATE")
    private Date updateDate;
    @Column(name = "NDISTANCEACTUAL")
    private Integer actualDistance;
    @Column(name ="NSTATUS")
    private String status;

    @Override
    public String toString() {
        return "DistanceInfo{" +
                "id=" + id +
                ", yearCode='" + yearCode + '\'' +
                ", slipNo=" + slipNo +
                ", shiftNumber=" + shiftNumber +
                ", slipDate=" + slipDate +
                ", plotNumber=" + plotNumber +
                ", token=" + token +
                ", distance=" + distance +
                ", vehicleType=" + vehicleType +
                ", harvestorCode=" + harvestorCode +
                ", transporterCode=" + transporterCode +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", bulluckcartCode=" + bulluckcartCode +
                ", bulluckcartMainCode=" + bulluckcartMainCode +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", actualDistance=" + actualDistance +
                ", status='" + status + '\'' +
                '}';
    }
}