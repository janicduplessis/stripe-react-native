import Foundation
import UIKit

@objc(StripeContainerView)
public class StripeContainerView: UIView {
    var tapRecognizer: UITapGestureRecognizer? = nil

    @objc public var keyboardShouldPersistTaps: Bool = true {
        didSet {
            if (keyboardShouldPersistTaps == true) {
                removeListener()
            } else {
                setListener()
            }
        }
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
    }

    func setListener() {
        removeListener()
        tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(endEditing(_:)))

        tapRecognizer!.cancelsTouchesInView = false
        self.addGestureRecognizer(tapRecognizer!)
    }

    func removeListener() {
        if let tapRecognizer = self.tapRecognizer {
            self.removeGestureRecognizer(tapRecognizer)
        }
        self.tapRecognizer = nil
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
