// access_token'ı sadece frontend içinde yönetmek için
// HTTP-only cookie kullanıldığı için frontend sadece dummy token'ı saklayabilir (opsiyonel)
let accessToken: string | null = null;

export const tokenStore = {
  getAccessToken: () => accessToken,
  setAccessToken: (token: string | null) => {
    accessToken = token;
    console.log("[tokenStore] setAccessToken ->", token);
  },
};
