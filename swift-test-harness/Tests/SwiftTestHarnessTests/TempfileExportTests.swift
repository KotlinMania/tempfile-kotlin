import Testing
import Tempfile

@Suite struct TempfileExportTests {
    @Test func testSwiftModuleLoads() {
        #expect(Bool(true), "Tempfile swift module imported cleanly")
    }
}
