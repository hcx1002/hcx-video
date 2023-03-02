import { request } from "@/config/request";

export function login(username: string, password: string) {
  return request.post<string>("user/login", { username, password });
}

export function regis(username: string, password: string) {
  return request.post<string>("user/regis", { username, password });
}
export function logout() {
  return request.get<string>("user/logout");
}
export function getUserInfo() {
  return request.get<User.Info>("user/info");
}
export function getVideoPages(params: Video.Page) {
  return request.post<Common.Page<Video.Info>>("video/page", { ...params });
}

export function getVideoById(videoId: string) {
  return request.get<Video.Info>("video/getVideoById?videoId=" + videoId);
}
export function setVideoView(videoId: string) {
  return request.get<null>("video/setVideoView?videoId=" + videoId);
}
export function setVideoInfo(params: Video.Update) {
  return request.post<Video.Info>("video/updateVideoInfo", { ...params });
}
export function delVideo(videoId: string) {
  return request.get<null>("video/delVideo?videoId=" + videoId);
}
export function getUserVideo(userId: string) {
  return request.get<Video.Info[]>("video/getUserVideo?userId=" + userId);
}
export function getMyVideo() {
  return request.get<Video.Info[]>("video/getUserVideoByMi");
}
export function uploadVideo(params: any) {
  return request.upload<Video.Info>("video/upload", params);
}
export function getTranscodingVideo(id:string,videoId:string) {
  return request.get<Video.Process>(`video/getTranscodingVideo/${id}?videoId=${videoId}`);
}