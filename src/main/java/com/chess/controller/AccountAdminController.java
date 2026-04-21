package com.chess.controller;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.service.AccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AccountAdminController - Quản lý người chơi (CRUD).
 * Chỉ dành cho ADMIN. Tương ứng Module III trong báo cáo.
 *
 * Các hàm chính:
 * - fetchAccountRegistry()   → Xem danh sách (có phân trang)
 * - showAddForm()            → Hiển thị form thêm mới
 * - registerNewPlayer()      → Lưu người chơi mới
 * - showEditForm()           → Hiển thị form sửa
 * - updatePlayer()           → Cập nhật thông tin
 * - deletePlayer()           → Xóa người chơi
 */
@Controller
@RequestMapping("/admin/accounts")
public class AccountAdminController {

    @Autowired
    private AccountManagerService accountManagerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * fetchAccountRegistry - Xem danh sách tất cả người chơi có phân trang.
     */
    @GetMapping
    public String fetchAccountRegistry(Model model,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Account> accounts = accountManagerService.retrieveAllProfiles(pageable);
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentPage", page);
        return "account_management";
    }

    /**
     * showAddForm - Hiển thị form thêm người chơi mới.
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("isEdit", false);
        return "account_form";
    }

    /**
     * registerNewPlayer - Lưu người chơi mới vào DB.
     */
    @PostMapping("/add")
    public String registerNewPlayer(@ModelAttribute Account account,
                                    RedirectAttributes redirectAttributes) {
        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (accountManagerService.existsByUserName(account.getUserName())) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/accounts/add";
        }

        // Mã hóa mật khẩu
        account.setPassWord(passwordEncoder.encode(account.getPassWord()));
        // Giá trị mặc định
        account.setEloPoint(account.getEloPoint() == 0 ? 1500 : account.getEloPoint());
        account.setState(AccountState.OFFLINE);
        if (account.getUserRole() == null || account.getUserRole().isEmpty()) {
            account.setUserRole("USER");
        }

        accountManagerService.commitAccountChange(account);
        redirectAttributes.addFlashAttribute("success", "Thêm người chơi thành công!");
        return "redirect:/admin/accounts";
    }

    /**
     * showEditForm - Hiển thị form sửa thông tin người chơi.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Account account = accountManagerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + id));
        model.addAttribute("account", account);
        model.addAttribute("isEdit", true);
        return "account_form";
    }

    /**
     * updatePlayer - Cập nhật thông tin người chơi.
     */
    @PostMapping("/edit/{id}")
    public String updatePlayer(@PathVariable Long id,
                               @ModelAttribute Account formAccount,
                               RedirectAttributes redirectAttributes) {
        Account existing = accountManagerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + id));

        existing.setFullName(formAccount.getFullName());
        existing.setEloPoint(formAccount.getEloPoint());
        existing.setUserRole(formAccount.getUserRole());

        // Chỉ đổi mật khẩu nếu người dùng nhập mới
        if (formAccount.getPassWord() != null && !formAccount.getPassWord().isEmpty()) {
            existing.setPassWord(passwordEncoder.encode(formAccount.getPassWord()));
        }

        accountManagerService.commitAccountChange(existing);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/admin/accounts";
    }

    /**
     * deletePlayer - Xóa người chơi khỏi hệ thống.
     */
    @GetMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        accountManagerService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Xóa người chơi thành công!");
        return "redirect:/admin/accounts";
    }
}
