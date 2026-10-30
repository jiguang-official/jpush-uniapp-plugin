import Foundation
func check(_ value: @autoclosure () -> Bool) { precondition(value()) }
check(JPushUpgradeCodec.validToken(nil))
check(JPushUpgradeCodec.decode(nil) == nil)
for invalid in ["", "0", "0x12", "zz", "AA BB", "ＡＡ"] { check(!JPushUpgradeCodec.validToken(invalid)) }
check(JPushUpgradeCodec.encode(JPushUpgradeCodec.decode("aA01fF")) == "aa01ff")
for invalid in [Double.nan,Double.infinity,1.2,Double(Int32.max)+1,Double(Int32.min)-1] { check(!JPushUpgradeCodec.validSequence(NSNumber(value:invalid))) }
check(JPushUpgradeCodec.validSequence(NSNumber(value:Int32.min)))
check(JPushUpgradeCodec.validSequence(NSNumber(value:Int32.max)))
check(!JPushUpgradeCodec.validIdentifier(" \n"))
check(JPushUpgradeCodec.validIdentifier("activity"))
let old = JPushUpgradeCodec.generation()
let group=DispatchGroup()
for _ in 0..<100 { group.enter();DispatchQueue.global().async { JPushUpgradeCodec.invalidate();group.leave() } }
group.wait();check(JPushUpgradeCodec.generation()==old+100)
print("PASS: token null/hex boundaries, signed32 seq, identifier, 100 concurrent invalidations")
