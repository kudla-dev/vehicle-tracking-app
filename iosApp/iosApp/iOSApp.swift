import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        KoinManagerKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(edges: .all)
        }
    }
}
