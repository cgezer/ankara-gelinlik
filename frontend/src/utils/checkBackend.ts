// src/utils/
export async function checkBackendStatus(): Promise<"up" | "down"> {
  try {
    const res = await fetch("http://localhost:8080/auth/me", {
      method: "GET",
      credentials: "include", // Cookie ile token gönder
    });

    // 401 Unauthorized: Backend çalışıyor, ancak kullanıcı oturum açmamış
    if (res.status === 401) {
      return "up"; // Backend çalışıyor ama oturum açılmamış
    }

    // Diğer durumlar: Eğer cevap başarılıysa (200-299 arası statü), backend düzgün çalışıyor
    if (res.ok) {
      return "up"; // Backend çalışıyor ve kullanıcı oturum açmış
    }

    // Diğer hata durumları: Backend çalışıyor, ancak hata alındı (500, 404, vb.)
    return "down"; // Backend çalışıyor ancak başka bir hata durumu oluştu (örneğin 500, 503, vb.)

  } catch (error) {
    // Eğer fetch isteği başarısız olursa (örneğin backend kapalıysa), backend "down" olarak kabul edilir.
    return "down"; // Backend offline
  }
}
