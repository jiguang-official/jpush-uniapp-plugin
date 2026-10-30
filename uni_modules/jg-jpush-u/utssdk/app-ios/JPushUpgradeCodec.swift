import Foundation

public class JPushUpgradeCodec {
    private static let lifecycleLock = NSLock()
    private static var lifecycle: Int64 = 0
    public static func generation() -> Int64 { lifecycleLock.lock(); defer { lifecycleLock.unlock() }; return lifecycle }
    public static func invalidate() { lifecycleLock.lock(); lifecycle += 1; lifecycleLock.unlock() }
    public static func validIdentifier(_ value: String) -> Bool { !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty }
    public static func validSequence(_ number: NSNumber) -> Bool { let value = number.doubleValue; return value.isFinite && value.rounded(.towardZero) == value && value >= Double(Int32.min) && value <= Double(Int32.max) }
    public static func validToken(_ value: String?) -> Bool {
        guard let value = value else { return true }
        return !value.isEmpty && value.utf8.count % 2 == 0 && value.utf8.allSatisfy { (48...57).contains($0) || (65...70).contains($0) || (97...102).contains($0) }
    }
    public static func decode(_ value: String?) -> Data? {
        guard let value = value, validToken(value) else { return nil }
        let bytes = Array(value.utf8)
        func nibble(_ b: UInt8) -> UInt8 { b <= 57 ? b - 48 : b <= 70 ? b - 55 : b - 87 }
        return Data(stride(from: 0, to: bytes.count, by: 2).map { nibble(bytes[$0]) * 16 + nibble(bytes[$0 + 1]) })
    }
    public static func encode(_ value: Data?) -> String? { value.map { $0.map { String(format: "%02x", $0) }.joined() } }
}
