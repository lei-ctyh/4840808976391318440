// 个人成绩年度考核 mock 数据
// 数据结构尽量贴近需求，后续可以直接替换为后端接口返回

function rand(min, max) {
  return Math.round(Math.random() * (max - min) + min);
}

const names = [
  "张三","李四","王五","赵六","钱七","孙八","周九","吴十",
  "郑一","冯二","陈三","褚四","卫五","蒋六","沈七","韩八"
];
const unitPaths = [
  "总校/一中/高一年级",
  "总校/二中/高二年级",
  "总校/实验/初一",
  "总校/三中/高三年级",
  "总校/附中/初二"
];
const titles = ["正高","副高","中级","初级"];

export function getLeaderAssessmentData(year) {
  const y = String(year || new Date().getFullYear());
  const list = Array.from({ length: 20 }).map((_, i) => {
    const name = names[i % names.length];
    const unitPath = unitPaths[i % unitPaths.length];
    const title = titles[i % titles.length];
    const age = rand(25, 58);
    const month = rand(1, 12);
    const birthdate = `${rand(1965, 2000)}-${String(month).padStart(2, '0')}`;

    const baseBasicKnowledge = rand(70, 95);
    const baseSportsTrack = rand(70, 95);
    const baseSportsRope = rand(70, 95);
    const baseSportsLongJump = rand(70, 95);
    const baseGroupA = rand(70, 95);
    const baseGroupB = rand(70, 95);
    // 简单合成一个基础科目成绩（仅示意）
    const baseTotal = Math.round(
      (baseBasicKnowledge * 0.2 +
        // 体育三项简单平均后占 30%
        ((baseSportsTrack + baseSportsRope + baseSportsLongJump) / 3) * 0.3 +
        baseGroupA * 0.25 +
        baseGroupB * 0.25) * 100
    ) / 100;

    const commonSubject = Array.from({ length: 8 }).map(() => rand(70, 95));
    const commonTotal = Math.round(
      (commonSubject.reduce((a, b) => a + b, 0) / 8) * 100
    ) / 100;

    const jobBusiness = rand(75, 98);
    const comprehensivePercent = Math.round(
      (baseTotal * 0.2 + commonTotal * 0.3 + jobBusiness * 0.5) * 100
    ) / 100;

    const comprehensiveLevel = comprehensivePercent >= 90
      ? "优秀"
      : comprehensivePercent >= 80
        ? "良好"
        : comprehensivePercent >= 60
          ? "合格"
          : "不合格";

    return {
      personId: `P${String(i + 1).padStart(3, '0')}`,
      name,
      unitPath,
      birthdate,
      age,
      title,
      cycle: `${y}年度`,
      baseBasicKnowledge,
      baseSportsTrack,
      baseSportsRope,
      baseSportsLongJump,
      baseGroupA,
      baseGroupB,
      baseTotal,
      commonSubject1: commonSubject[0],
      commonSubject2: commonSubject[1],
      commonSubject3: commonSubject[2],
      commonSubject4: commonSubject[3],
      commonSubject5: commonSubject[4],
      commonSubject6: commonSubject[5],
      commonSubject7: commonSubject[6],
      commonSubject8: commonSubject[7],
      commonTotal,
      jobBusiness,
      comprehensivePercent,
      comprehensiveLevel,
      totalScore: comprehensivePercent, // 总成绩与综合成绩相同
      totalRating: comprehensiveLevel,  // 总评定与综合评定相同
      remark: "",
      description: ""
    };
  });
  return list;
}
