package com.example.demo.config.messageConfig;

import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageBody {

    public String getTaskRemovalBody(TaskAssignment taskAssignment) {
        String message = "<html>" +
                "<body>" +
                "<p>Hello <strong>" + taskAssignment.getUser().getDisplayName() + "</strong>,</p>" +
                "<p>The task <strong>'" + taskAssignment.getTask().getTitle() + "'</strong> has been <span style='color:red;'>unassigned</span> from you.</p>" +
                "<p>If you have any questions, please contact your manager.</p>" +
                "<br>" +
                "<p>Best regards,<br>Task Management Team</p>" +
                "</body>" +
                "</html>";

        return message;
    }

    public String getTaskAssignmentBody(TaskAssignment taskAssignment) {
        String message = "<html>" +
                "<body>" +
                "<p>Hello <strong>" + taskAssignment.getUser().getDisplayName() + "</strong>,</p>" +
                "<p>The task <strong>'" + taskAssignment.getTask().getTitle() + "'</strong> has been <span style='color:green;'>assigned</span> to you.</p>" +
                "<p>If you have any questions, please contact your manager.</p>" +
                "<br>" +
                "<p>Best regards,<br>Task Flow Team</p>" +
                "</body>" +
                "</html>";

        return message;
    }

    public String getUserRegistrationBody(String userName, String password, String displayName) {
       String message =  "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
                "    <h2 style=\"color: #007bff;\">Welcome to TeamFlow!</h2>\n" +
                "    <p>Hello <strong>"+displayName+"</strong>,</p>\n" +
                "    <p>We're excited to have you on board. Here are your login details:</p>\n" +
                "    \n" +
                "    <table style=\"border-collapse: collapse; width: 100%;\">\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 8px; border: 1px solid #ddd;\"><strong>Username:</strong></td>\n" +
                "            <td style=\"padding: 8px; border: 1px solid #ddd;\">"+ userName +"</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 8px; border: 1px solid #ddd;\"><strong>Password:</strong></td>\n" +
                "            <td style=\"padding: 8px; border: 1px solid #ddd;\">"+password+"</td>\n" +
                "        </tr>\n" +
                "    </table>\n";
       return message;
    }
    public String getPasswordResetBody(String displayName, String resetLink) {
        return "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
                "    <h2 style=\"color: #007bff;\">Password Reset Request</h2>\n" +
                "    <p>Hello <strong>" + displayName + "</strong>,</p>\n" +
                "    <p>We received a request to reset your password. Click the button below to reset it:</p>\n" +
                "    <p style=\"text-align: center;\">\n" +
                "        <a href=\"" + resetLink + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; " +
                "            color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;\">\n" +
                "            Reset Password\n" +
                "        </a>\n" +
                "    </p>\n" +
                "    <p>If you didn't request a password reset, you can ignore this email. Your password will remain unchanged.</p>\n" +
                "    <hr>\n" +
                "    <p style=\"font-size: 12px; color: #777;\">This link will expire in 15 minutes. If you need help, please contact support.</p>\n" +
                "</div>";
    }
}
