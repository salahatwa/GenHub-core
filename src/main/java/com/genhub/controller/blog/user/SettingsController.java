//package com.mboot.controller.blog.user;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.mboot.config.secuirty.CurrentUser;
//import com.mboot.config.secuirty.UserPrincipal;
//import com.mboot.domain.dto.Result;
//import com.mboot.domain.dto.blog.AccountProfile;
//import com.mboot.domain.dto.blog.UserVO;
//import com.mboot.service.blog.SecurityCodeService;
//import com.mboot.service.blog.UserService;
//import com.mboot.storage.StorageFactory;
//import com.mboot.utils.BlogConsts;
//import com.mboot.utils.FileKit;
//import com.mboot.utils.FilePathUtils;
//import com.mboot.utils.ImageUtils;
//
//@Controller
//@RequestMapping("/settings")
//public class SettingsController {
//	@Autowired
//	private UserService userService;
//	@Autowired
//	private SecurityCodeService securityCodeService;
//	@Autowired
//	protected StorageFactory storageFactory;
//
//	@GetMapping(value = "/profile")
//	public String view(@CurrentUser UserPrincipal currentUser, ModelMap model) {
//		UserVO view = userService.get(currentUser.getId());
//		model.put("view", view);
////        return view(Views.SETTINGS_PROFILE);
//		return null;
//	}
//
//	@GetMapping(value = "/email")
//	public String email() {
////		return view(Views.SETTINGS_EMAIL);
//		return null;
//	}
//
//	@GetMapping(value = "/avatar")
//	public String avatar() {
////		return view(Views.SETTINGS_AVATAR);
//		return null;
//	}
//
//	@GetMapping(value = "/password")
//	public String password() {
////		return view(Views.SETTINGS_PASSWORD);
//		return null;
//	}
//
//	@PostMapping(value = "/profile")
//	public String updateProfile(@CurrentUser UserPrincipal currentUser, String name, String signature, ModelMap model) {
//		Result data;
//
//		try {
//			UserVO user = new UserVO();
//			user.setId(currentUser.getId());
//			user.setName(name);
//			user.setSignature(signature);
//
//			putProfile(userService.update(user));
//
//			UserVO view = userService.get(currentUser.getId());
//			model.put("view", view);
//
//			data = Result.success();
//		} catch (Exception e) {
//			data = Result.failure(e.getMessage());
//		}
//		model.put("data", data);
////		return view(Views.SETTINGS_PROFILE);
//		return null;
//	}
//
//	@PostMapping(value = "/email")
//	public String updateEmail(@CurrentUser UserPrincipal currentUser, String email, String code, ModelMap model) {
//		Result data;
//		try {
//			Assert.hasLength(email, "Please input the email address");
//			Assert.hasLength(code, "Please enter verification code");
//
//			securityCodeService.verify(String.valueOf(currentUser.getId()), BlogConsts.CODE_BIND, code);
//			AccountProfile p = userService.updateEmail(currentUser.getId(), email);
//			putProfile(p);
//
//			data = Result.success();
//		} catch (Exception e) {
//			data = Result.failure(e.getMessage());
//		}
//		model.put("data", data);
////		return view(Views.SETTINGS_EMAIL);
//		return null;
//	}
//
//	@PostMapping(value = "/password")
//	public String updatePassword(@CurrentUser UserPrincipal currentUser, String oldPassword, String password,
//			ModelMap model) {
//		Result data;
//		try {
//			userService.updatePassword(currentUser.getId(), oldPassword, password);
//
//			data = Result.success();
//		} catch (Exception e) {
//			data = Result.failure(e.getMessage());
//		}
//		model.put("data", data);
////		return view(Views.SETTINGS_PASSWORD);
//		return null;
//	}
//
//	@PostMapping("/avatar")
//	@ResponseBody
//	public UploadController.UploadResult updateAvatar(@CurrentUser UserPrincipal currentUser,
//			@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
//		UploadController.UploadResult result = new UploadController.UploadResult();
//
//		if (null == file || file.isEmpty()) {
//			return result.error(UploadController.errorInfo.get("NOFILE"));
//		}
//
//		String fileName = file.getOriginalFilename();
//
//		if (!FileKit.checkFileType(fileName)) {
//			return result.error(UploadController.errorInfo.get("TYPE"));
//		}
//
//		try {
//			String ava100 = BlogConsts.avatarPath + getAvaPath(currentUser.getId(), 240);
//			byte[] bytes = ImageUtils.screenshot(file, 240, 240);
//			String path = storageFactory.get().writeToStore(bytes, ava100);
//
//			AccountProfile user = userService.updateAvatar(currentUser.getId(), path);
//			putProfile(user);
//
//			result.ok(UploadController.errorInfo.get("SUCCESS"));
//			result.setName(fileName);
//			result.setPath(path);
//			result.setSize(file.getSize());
//		} catch (Exception e) {
//			result.error(UploadController.errorInfo.get("UNKNOWN"));
//		}
////		return result;
//		return null;
//	}
//
//	private String getAvaPath(long uid, int size) {
//		String base = FilePathUtils.getAvatar(uid);
//		return String.format("/%s_%d.jpg", base, size);
//	}
//}
