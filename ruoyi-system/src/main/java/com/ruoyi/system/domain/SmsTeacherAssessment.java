package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师成绩考核对象 sms_teacher_assessment
 * 
 * @author ruoyi
 * @date 2025-01-10
 */
public class SmsTeacherAssessment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员编号 */
    @Excel(name = "人员编号")
    private String personId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String personName;

    /** 单位编号 */
    @Excel(name = "单位编号")
    private String unitId;

    /** 出生年月 */
    @Excel(name = "出生年月", dateFormat = "yyyy-MM")
    private String birthDate;

    /** 年龄 */
    @Excel(name = "年龄")
    private String age;

    /** 衔级 */
    @Excel(name = "衔级")
    private String title;

    /** 评定周期 */
    @Excel(name = "评定周期")
    private String period;

    /** 基础科目各项成绩 (metric_001 到 metric_100) */
    private String metric001;
    private String metric002;
    private String metric003;
    private String metric004;
    private String metric005;
    private String metric006;
    private String metric007;
    private String metric008;
    private String metric009;
    private String metric010;
    private String metric011;
    private String metric012;
    private String metric013;
    private String metric014;
    private String metric015;
    private String metric016;
    private String metric017;
    private String metric018;
    private String metric019;
    private String metric020;
    private String metric021;
    private String metric022;
    private String metric023;
    private String metric024;
    private String metric025;
    private String metric026;
    private String metric027;
    private String metric028;
    private String metric029;
    private String metric030;
    private String metric031;
    private String metric032;
    private String metric033;
    private String metric034;
    private String metric035;
    private String metric036;
    private String metric037;
    private String metric038;
    private String metric039;
    private String metric040;
    private String metric041;
    private String metric042;
    private String metric043;
    private String metric044;
    private String metric045;
    private String metric046;
    private String metric047;
    private String metric048;
    private String metric049;
    private String metric050;
    private String metric051;
    private String metric052;
    private String metric053;
    private String metric054;
    private String metric055;
    private String metric056;
    private String metric057;
    private String metric058;
    private String metric059;
    private String metric060;
    private String metric061;
    private String metric062;
    private String metric063;
    private String metric064;
    private String metric065;
    private String metric066;
    private String metric067;
    private String metric068;
    private String metric069;
    private String metric070;
    private String metric071;
    private String metric072;
    private String metric073;
    private String metric074;
    private String metric075;
    private String metric076;
    private String metric077;
    private String metric078;
    private String metric079;
    private String metric080;
    private String metric081;
    private String metric082;
    private String metric083;
    private String metric084;
    private String metric085;
    private String metric086;
    private String metric087;
    private String metric088;
    private String metric089;
    private String metric090;
    private String metric091;
    private String metric092;
    private String metric093;
    private String metric094;
    private String metric095;
    private String metric096;
    private String metric097;
    private String metric098;
    private String metric099;
    private String metric100;

    /** 总成绩 */
    @Excel(name = "总成绩")
    private String totalScore;

    /** 总评定 */
    @Excel(name = "总评定")
    private String totalRating;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    public void setPersonId(String personId) 
    {
        this.personId = personId;
    }

    public String getPersonId() 
    {
        return personId;
    }
    public void setPersonName(String personName) 
    {
        this.personName = personName;
    }

    public String getPersonName() 
    {
        return personName;
    }
    public void setUnitId(String unitId) 
    {
        this.unitId = unitId;
    }

    public String getUnitId() 
    {
        return unitId;
    }
    public void setBirthDate(String birthDate) 
    {
        this.birthDate = birthDate;
    }

    public String getBirthDate() 
    {
        return birthDate;
    }
    public void setAge(String age) 
    {
        this.age = age;
    }

    public String getAge() 
    {
        return age;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setPeriod(String period) 
    {
        this.period = period;
    }

    public String getPeriod() 
    {
        return period;
    }

    // Metric getters and setters
    public String getMetric001() { return metric001; }
    public void setMetric001(String metric001) { this.metric001 = metric001; }
    
    public String getMetric002() { return metric002; }
    public void setMetric002(String metric002) { this.metric002 = metric002; }
    
    public String getMetric003() { return metric003; }
    public void setMetric003(String metric003) { this.metric003 = metric003; }
    
    public String getMetric004() { return metric004; }
    public void setMetric004(String metric004) { this.metric004 = metric004; }
    
    public String getMetric005() { return metric005; }
    public void setMetric005(String metric005) { this.metric005 = metric005; }
    
    public String getMetric006() { return metric006; }
    public void setMetric006(String metric006) { this.metric006 = metric006; }
    
    public String getMetric007() { return metric007; }
    public void setMetric007(String metric007) { this.metric007 = metric007; }
    
    public String getMetric008() { return metric008; }
    public void setMetric008(String metric008) { this.metric008 = metric008; }
    
    public String getMetric009() { return metric009; }
    public void setMetric009(String metric009) { this.metric009 = metric009; }
    
    public String getMetric010() { return metric010; }
    public void setMetric010(String metric010) { this.metric010 = metric010; }
    
    public String getMetric011() { return metric011; }
    public void setMetric011(String metric011) { this.metric011 = metric011; }
    
    public String getMetric012() { return metric012; }
    public void setMetric012(String metric012) { this.metric012 = metric012; }
    
    public String getMetric013() { return metric013; }
    public void setMetric013(String metric013) { this.metric013 = metric013; }
    
    public String getMetric014() { return metric014; }
    public void setMetric014(String metric014) { this.metric014 = metric014; }
    
    public String getMetric015() { return metric015; }
    public void setMetric015(String metric015) { this.metric015 = metric015; }
    
    public String getMetric016() { return metric016; }
    public void setMetric016(String metric016) { this.metric016 = metric016; }
    
    public String getMetric017() { return metric017; }
    public void setMetric017(String metric017) { this.metric017 = metric017; }
    
    public String getMetric018() { return metric018; }
    public void setMetric018(String metric018) { this.metric018 = metric018; }
    
    public String getMetric019() { return metric019; }
    public void setMetric019(String metric019) { this.metric019 = metric019; }
    
    public String getMetric020() { return metric020; }
    public void setMetric020(String metric020) { this.metric020 = metric020; }
    
    public String getMetric021() { return metric021; }
    public void setMetric021(String metric021) { this.metric021 = metric021; }
    
    public String getMetric022() { return metric022; }
    public void setMetric022(String metric022) { this.metric022 = metric022; }
    
    public String getMetric023() { return metric023; }
    public void setMetric023(String metric023) { this.metric023 = metric023; }
    
    public String getMetric024() { return metric024; }
    public void setMetric024(String metric024) { this.metric024 = metric024; }
    
    public String getMetric025() { return metric025; }
    public void setMetric025(String metric025) { this.metric025 = metric025; }
    
    public String getMetric026() { return metric026; }
    public void setMetric026(String metric026) { this.metric026 = metric026; }
    
    public String getMetric027() { return metric027; }
    public void setMetric027(String metric027) { this.metric027 = metric027; }
    
    public String getMetric028() { return metric028; }
    public void setMetric028(String metric028) { this.metric028 = metric028; }
    
    public String getMetric029() { return metric029; }
    public void setMetric029(String metric029) { this.metric029 = metric029; }
    
    public String getMetric030() { return metric030; }
    public void setMetric030(String metric030) { this.metric030 = metric030; }
    
    public String getMetric031() { return metric031; }
    public void setMetric031(String metric031) { this.metric031 = metric031; }
    
    public String getMetric032() { return metric032; }
    public void setMetric032(String metric032) { this.metric032 = metric032; }
    
    public String getMetric033() { return metric033; }
    public void setMetric033(String metric033) { this.metric033 = metric033; }
    
    public String getMetric034() { return metric034; }
    public void setMetric034(String metric034) { this.metric034 = metric034; }
    
    public String getMetric035() { return metric035; }
    public void setMetric035(String metric035) { this.metric035 = metric035; }
    
    public String getMetric036() { return metric036; }
    public void setMetric036(String metric036) { this.metric036 = metric036; }
    
    public String getMetric037() { return metric037; }
    public void setMetric037(String metric037) { this.metric037 = metric037; }
    
    public String getMetric038() { return metric038; }
    public void setMetric038(String metric038) { this.metric038 = metric038; }
    
    public String getMetric039() { return metric039; }
    public void setMetric039(String metric039) { this.metric039 = metric039; }
    
    public String getMetric040() { return metric040; }
    public void setMetric040(String metric040) { this.metric040 = metric040; }
    
    public String getMetric041() { return metric041; }
    public void setMetric041(String metric041) { this.metric041 = metric041; }
    
    public String getMetric042() { return metric042; }
    public void setMetric042(String metric042) { this.metric042 = metric042; }
    
    public String getMetric043() { return metric043; }
    public void setMetric043(String metric043) { this.metric043 = metric043; }
    
    public String getMetric044() { return metric044; }
    public void setMetric044(String metric044) { this.metric044 = metric044; }
    
    public String getMetric045() { return metric045; }
    public void setMetric045(String metric045) { this.metric045 = metric045; }
    
    public String getMetric046() { return metric046; }
    public void setMetric046(String metric046) { this.metric046 = metric046; }
    
    public String getMetric047() { return metric047; }
    public void setMetric047(String metric047) { this.metric047 = metric047; }
    
    public String getMetric048() { return metric048; }
    public void setMetric048(String metric048) { this.metric048 = metric048; }
    
    public String getMetric049() { return metric049; }
    public void setMetric049(String metric049) { this.metric049 = metric049; }
    
    public String getMetric050() { return metric050; }
    public void setMetric050(String metric050) { this.metric050 = metric050; }
    
    public String getMetric051() { return metric051; }
    public void setMetric051(String metric051) { this.metric051 = metric051; }
    
    public String getMetric052() { return metric052; }
    public void setMetric052(String metric052) { this.metric052 = metric052; }
    
    public String getMetric053() { return metric053; }
    public void setMetric053(String metric053) { this.metric053 = metric053; }
    
    public String getMetric054() { return metric054; }
    public void setMetric054(String metric054) { this.metric054 = metric054; }
    
    public String getMetric055() { return metric055; }
    public void setMetric055(String metric055) { this.metric055 = metric055; }
    
    public String getMetric056() { return metric056; }
    public void setMetric056(String metric056) { this.metric056 = metric056; }
    
    public String getMetric057() { return metric057; }
    public void setMetric057(String metric057) { this.metric057 = metric057; }
    
    public String getMetric058() { return metric058; }
    public void setMetric058(String metric058) { this.metric058 = metric058; }
    
    public String getMetric059() { return metric059; }
    public void setMetric059(String metric059) { this.metric059 = metric059; }
    
    public String getMetric060() { return metric060; }
    public void setMetric060(String metric060) { this.metric060 = metric060; }
    
    public String getMetric061() { return metric061; }
    public void setMetric061(String metric061) { this.metric061 = metric061; }
    
    public String getMetric062() { return metric062; }
    public void setMetric062(String metric062) { this.metric062 = metric062; }
    
    public String getMetric063() { return metric063; }
    public void setMetric063(String metric063) { this.metric063 = metric063; }
    
    public String getMetric064() { return metric064; }
    public void setMetric064(String metric064) { this.metric064 = metric064; }
    
    public String getMetric065() { return metric065; }
    public void setMetric065(String metric065) { this.metric065 = metric065; }
    
    public String getMetric066() { return metric066; }
    public void setMetric066(String metric066) { this.metric066 = metric066; }
    
    public String getMetric067() { return metric067; }
    public void setMetric067(String metric067) { this.metric067 = metric067; }
    
    public String getMetric068() { return metric068; }
    public void setMetric068(String metric068) { this.metric068 = metric068; }
    
    public String getMetric069() { return metric069; }
    public void setMetric069(String metric069) { this.metric069 = metric069; }
    
    public String getMetric070() { return metric070; }
    public void setMetric070(String metric070) { this.metric070 = metric070; }
    
    public String getMetric071() { return metric071; }
    public void setMetric071(String metric071) { this.metric071 = metric071; }
    
    public String getMetric072() { return metric072; }
    public void setMetric072(String metric072) { this.metric072 = metric072; }
    
    public String getMetric073() { return metric073; }
    public void setMetric073(String metric073) { this.metric073 = metric073; }
    
    public String getMetric074() { return metric074; }
    public void setMetric074(String metric074) { this.metric074 = metric074; }
    
    public String getMetric075() { return metric075; }
    public void setMetric075(String metric075) { this.metric075 = metric075; }
    
    public String getMetric076() { return metric076; }
    public void setMetric076(String metric076) { this.metric076 = metric076; }
    
    public String getMetric077() { return metric077; }
    public void setMetric077(String metric077) { this.metric077 = metric077; }
    
    public String getMetric078() { return metric078; }
    public void setMetric078(String metric078) { this.metric078 = metric078; }
    
    public String getMetric079() { return metric079; }
    public void setMetric079(String metric079) { this.metric079 = metric079; }
    
    public String getMetric080() { return metric080; }
    public void setMetric080(String metric080) { this.metric080 = metric080; }
    
    public String getMetric081() { return metric081; }
    public void setMetric081(String metric081) { this.metric081 = metric081; }
    
    public String getMetric082() { return metric082; }
    public void setMetric082(String metric082) { this.metric082 = metric082; }
    
    public String getMetric083() { return metric083; }
    public void setMetric083(String metric083) { this.metric083 = metric083; }
    
    public String getMetric084() { return metric084; }
    public void setMetric084(String metric084) { this.metric084 = metric084; }
    
    public String getMetric085() { return metric085; }
    public void setMetric085(String metric085) { this.metric085 = metric085; }
    
    public String getMetric086() { return metric086; }
    public void setMetric086(String metric086) { this.metric086 = metric086; }
    
    public String getMetric087() { return metric087; }
    public void setMetric087(String metric087) { this.metric087 = metric087; }
    
    public String getMetric088() { return metric088; }
    public void setMetric088(String metric088) { this.metric088 = metric088; }
    
    public String getMetric089() { return metric089; }
    public void setMetric089(String metric089) { this.metric089 = metric089; }
    
    public String getMetric090() { return metric090; }
    public void setMetric090(String metric090) { this.metric090 = metric090; }
    
    public String getMetric091() { return metric091; }
    public void setMetric091(String metric091) { this.metric091 = metric091; }
    
    public String getMetric092() { return metric092; }
    public void setMetric092(String metric092) { this.metric092 = metric092; }
    
    public String getMetric093() { return metric093; }
    public void setMetric093(String metric093) { this.metric093 = metric093; }
    
    public String getMetric094() { return metric094; }
    public void setMetric094(String metric094) { this.metric094 = metric094; }
    
    public String getMetric095() { return metric095; }
    public void setMetric095(String metric095) { this.metric095 = metric095; }
    
    public String getMetric096() { return metric096; }
    public void setMetric096(String metric096) { this.metric096 = metric096; }
    
    public String getMetric097() { return metric097; }
    public void setMetric097(String metric097) { this.metric097 = metric097; }
    
    public String getMetric098() { return metric098; }
    public void setMetric098(String metric098) { this.metric098 = metric098; }
    
    public String getMetric099() { return metric099; }
    public void setMetric099(String metric099) { this.metric099 = metric099; }
    
    public String getMetric100() { return metric100; }
    public void setMetric100(String metric100) { this.metric100 = metric100; }

    public void setTotalScore(String totalScore) 
    {
        this.totalScore = totalScore;
    }

    public String getTotalScore() 
    {
        return totalScore;
    }

    public void setTotalRating(String totalRating) 
    {
        this.totalRating = totalRating;
    }

    public String getTotalRating() 
    {
        return totalRating;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("personId", getPersonId())
            .append("personName", getPersonName())
            .append("unitId", getUnitId())
            .append("birthDate", getBirthDate())
            .append("age", getAge())
            .append("title", getTitle())
            .append("period", getPeriod())
            .append("totalScore", getTotalScore())
            .append("totalRating", getTotalRating())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}